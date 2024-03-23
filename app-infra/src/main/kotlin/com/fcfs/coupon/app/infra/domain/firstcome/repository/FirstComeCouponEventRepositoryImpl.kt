package com.fcfs.coupon.app.infra.domain.firstcome.repository

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcome.command.dto.FirstComeCouponEventEntryResult
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import com.fcfs.coupon.app.infra.domain.coupon.dao.CouponJpaDao
import com.fcfs.coupon.app.infra.domain.firstcome.dao.FirstComeCouponEventEntityJpaDao
import com.fcfs.coupon.app.infra.domain.firstcome.dao.FirstComeCouponEventRedisDao
import com.fcfs.coupon.app.infra.domain.firstcome.entity.FirstComeCouponEventEntity
import com.fcfs.coupon.app.infra.domain.user.dao.UserJpaDao
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/*
 * redis를 선택한 이유는 레디스가 원자성보장을 제공하여 선착순 구현에 가장 적합하다 생각하였습니다
 * 출처 : https://redis.io/docs/about/
 */
@Repository
internal class FirstComeCouponEventRepositoryImpl(
    val jpaDao: FirstComeCouponEventEntityJpaDao,
    val redisDao: FirstComeCouponEventRedisDao,
    val couponDao: CouponJpaDao,
    val userDao: UserJpaDao
) : FirstComeCouponEventRepository {
    @Transactional
    override fun save(firstComeCouponEvent: FirstComeCouponEvent): FirstComeCouponEvent {
        return jpaDao.save(firstComeCouponEvent.toEntity()).toDomain()
    }

    override fun applyForFirstComeCouponEvent(id: FirstComeCouponEventId, date: LocalDate): FirstComeCouponEventEntryResult {
        return redisDao.applyForFirstComeCouponEvent(id.value, date)?.let {
            FirstComeCouponEventEntryResult(it.order, CouponId(it.couponId), true)
        } ?: FirstComeCouponEventEntryResult(null, null, false)
    }

    @Transactional(readOnly = true)
    override fun findById(id: FirstComeCouponEventId): FirstComeCouponEvent? {
        return jpaDao.findByIdOrNull(id.value)?.toDomain()
    }

    @Transactional(readOnly = true)
    override fun getById(id: FirstComeCouponEventId): FirstComeCouponEvent {
        return findById(id) ?: throw CustomException(ErrorCode.FC_COUPON_EVENT_NOT_FOUND)
    }

    private fun FirstComeCouponEvent.toEntity(): FirstComeCouponEventEntity {
        val coupons =
            couponDao.findAllById(
                listOf(
                    this.defaultCouponId.value,
                    this.specialCouponId.value,
                    this.consecutiveCouponId.value
                )
            )
        return FirstComeCouponEventEntity(
            eventId = this.id.value,
            name = this.name,
            description = this.description,
            limitCount = this.limitCount,
            specialLimitCount = this.specialLimitCount,
            history = mutableSetOf(),
            defaultCoupon = coupons.first { it.couponId == this.defaultCouponId.value },
            specialCoupon = coupons.first { it.couponId == this.specialCouponId.value },
            consecutiveCoupon = coupons.first { it.couponId == this.consecutiveCouponId.value },
            startDate = this.startDate,
            endDate = this.endDate,
        )
    }

    private fun FirstComeCouponEventEntity.toDomain(): FirstComeCouponEvent {
        return FirstComeCouponEvent(
            id = FirstComeCouponEventId(this.eventId),
            name = this.name,
            description = this.description,
            limitCount = this.limitCount,
            specialLimitCount = this.specialLimitCount,
            defaultCouponId = CouponId(
                this.defaultCoupon.couponId ?: throw IllegalStateException("coupon id is null")
            ),
            specialCouponId = CouponId(
                this.specialCoupon.couponId ?: throw IllegalStateException("coupon id is null")
            ),
            consecutiveCouponId = CouponId(
                this.consecutiveCoupon.couponId ?: throw IllegalStateException("coupon id is null")
            ),
            startDate = this.startDate,
            endDate = this.endDate,
        )
    }

}
