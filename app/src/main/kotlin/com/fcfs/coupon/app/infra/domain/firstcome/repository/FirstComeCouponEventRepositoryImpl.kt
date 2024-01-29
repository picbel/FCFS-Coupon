package com.fcfs.coupon.app.infra.domain.firstcome.repository

import com.fcfs.coupon.core.common.exception.CustomException
import com.fcfs.coupon.core.common.exception.ErrorCode
import com.fcfs.coupon.core.domain.firstcome.FirstComeCouponEvent
import com.fcfs.coupon.core.domain.firstcome.dto.FirstComeCouponEventEntryResult
import com.fcfs.coupon.app.core.domain.firstcome.model.FirstComeCouponEventHistory
import com.fcfs.coupon.core.domain.firstcome.model.FirstComeCouponSupplyHistory
import com.fcfs.coupon.core.domain.firstcome.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.infra.domain.coupon.dao.CouponJpaDao
import com.fcfs.coupon.app.infra.domain.firstcome.dao.FirstComeCouponEventEntityJpaDao
import com.fcfs.coupon.infra.domain.firstcome.dao.FirstComeCouponEventRedisDao
import com.fcfs.coupon.infra.domain.firstcome.entity.FirstComeCouponEventEntity
import com.fcfs.coupon.infra.domain.firstcome.entity.FirstComeCouponEventHistoryEntity
import com.fcfs.coupon.infra.domain.firstcome.entity.FirstComeCouponEventHistoryId
import com.fcfs.coupon.infra.domain.firstcome.entity.FirstComeCouponSupplyHistoryEntity
import com.fcfs.coupon.infra.domain.firstcome.entity.FirstComeCouponSupplyHistoryId
import com.fcfs.coupon.app.infra.domain.user.dao.UserJpaDao
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

/*
 * redis를 선택한 이유는 레디스가 원자성보장을 제공하여 선착순 구현에 가장 적합하다 생각하였습니다
 * 출처 : https://redis.io/docs/about/
 */
@Repository
internal class FirstComeCouponEventRepositoryImpl(
    val jpaDao: com.fcfs.coupon.app.infra.domain.firstcome.dao.FirstComeCouponEventEntityJpaDao,
    val redisDao: FirstComeCouponEventRedisDao,
    val couponDao: com.fcfs.coupon.app.infra.domain.coupon.dao.CouponJpaDao,
    val userDao: com.fcfs.coupon.app.infra.domain.user.dao.UserJpaDao
) : FirstComeCouponEventRepository {
    @Transactional
    override fun save(firstComeCouponEvent: FirstComeCouponEvent): FirstComeCouponEvent {
        return jpaDao.save(firstComeCouponEvent.toEntity()).toDomain()
    }

    override fun applyForFirstComeCouponEvent(id: UUID, date: LocalDate): FirstComeCouponEventEntryResult {
        return redisDao.applyForFirstComeCouponEvent(id, date)?.let {
            FirstComeCouponEventEntryResult(it.order, it.couponId, true)
        } ?: FirstComeCouponEventEntryResult(null, null, false)
    }

    @Transactional
    override fun findById(id: UUID): FirstComeCouponEvent? {
        return jpaDao.findByIdOrNull(id)?.toDomain()
    }

    @Transactional
    override fun getById(id: UUID): FirstComeCouponEvent {
        return findById(id) ?: throw CustomException(ErrorCode.FC_COUPON_EVENT_NOT_FOUND)
    }

    private fun FirstComeCouponEvent.toEntity(): FirstComeCouponEventEntity {
        val coupons =
            couponDao.findAllById(listOf(this.defaultCouponId, this.specialCouponId, this.consecutiveCouponId))
        return FirstComeCouponEventEntity(
            eventId = this.id,
            name = this.name,
            description = this.description,
            limitCount = this.limitCount,
            specialLimitCount = this.specialLimitCount,
            history = mutableSetOf(),
            defaultCoupon = coupons.first { it.couponId == this.defaultCouponId },
            specialCoupon = coupons.first { it.couponId == this.specialCouponId },
            consecutiveCoupon = coupons.first { it.couponId == this.consecutiveCouponId },
            startDate = this.startDate,
            endDate = this.endDate,
        ).apply {
            this.history.addAll(this@toEntity.history.toEntities(fcEvent = this))
        }
    }

    private fun Collection<com.fcfs.coupon.app.core.domain.firstcome.model.FirstComeCouponEventHistory>.toEntities(fcEvent: FirstComeCouponEventEntity): MutableSet<FirstComeCouponEventHistoryEntity> {
        val entities = map {
            FirstComeCouponEventHistoryEntity(
                id = FirstComeCouponEventHistoryId(
                    fcEventId = fcEvent.eventId,
                    eventDate = it.date,
                ),
                fcEvent = fcEvent,
                supplyHistory = mutableSetOf(),
            )
        }.toMutableSet()
        val supplyHistoryEntities = this.flatMap { it.supplyHistory }.toEntities(entities)
        return entities.onEach { eventHistoryEntity ->
            eventHistoryEntity.supplyHistory.addAll(supplyHistoryEntities.filter {
                it.id.eventHistoryId == eventHistoryEntity.id
            })
        }
    }

    private fun Collection<FirstComeCouponSupplyHistory>.toEntities(
        fcEventHistoryEntities: Collection<FirstComeCouponEventHistoryEntity>,
    ): MutableSet<FirstComeCouponSupplyHistoryEntity> {
        val users = userDao.findAllById(this.map { it.userId })
        val coupons = couponDao.findAllById(this.map { it.couponId })
        return map { history ->
            val fcEventHistory = fcEventHistoryEntities.first {
                it.id.eventDate == history.supplyDateTime.toLocalDate()
            }
            val user = users.first { it.userId == history.userId }
            val coupon = coupons.first { it.couponId == history.couponId }
            FirstComeCouponSupplyHistoryEntity(
                id = FirstComeCouponSupplyHistoryId(
                    userId = user.userId ?: throw IllegalStateException("user id is null"),
                    couponId = coupon.couponId ?: throw IllegalStateException("coupon id is null"),
                    eventHistoryId = fcEventHistory.id,
                ),
                fcEventHistory = fcEventHistory,
                user = user,
                coupon = coupon,
                supplyDateTime = history.supplyDateTime,
                continuousReset = history.continuousReset,
            )
        }.toMutableSet()
    }

    private fun FirstComeCouponEventEntity.toDomain(): FirstComeCouponEvent {
        return FirstComeCouponEvent(
            id = this.eventId,
            name = this.name,
            description = this.description,
            limitCount = this.limitCount,
            specialLimitCount = this.specialLimitCount,
            history = this.history.toEventHistoryEntities(),
            defaultCouponId = this.defaultCoupon.couponId ?: throw IllegalStateException("coupon id is null"),
            specialCouponId = this.specialCoupon.couponId ?: throw IllegalStateException("coupon id is null"),
            consecutiveCouponId = this.consecutiveCoupon.couponId ?: throw IllegalStateException("coupon id is null"),
            startDate = this.startDate,
            endDate = this.endDate,
        )
    }

    private fun Collection<FirstComeCouponEventHistoryEntity>.toEventHistoryEntities(): List<com.fcfs.coupon.app.core.domain.firstcome.model.FirstComeCouponEventHistory> {
        return this.map {
            com.fcfs.coupon.app.core.domain.firstcome.model.FirstComeCouponEventHistory(
                firstComeCouponEventId = it.id.fcEventId,
                date = it.id.eventDate,
                supplyHistory = it.supplyHistory.toSupplyHistoryEntities(),
            )
        }.toList()
    }

    private fun Collection<FirstComeCouponSupplyHistoryEntity>.toSupplyHistoryEntities(): List<FirstComeCouponSupplyHistory> {
        return this.map {
            FirstComeCouponSupplyHistory(
                userId = it.id.userId,
                couponId = it.id.couponId,
                supplyDateTime = it.supplyDateTime,
                continuousReset = it.continuousReset,
            )
        }.toList()
    }
}
