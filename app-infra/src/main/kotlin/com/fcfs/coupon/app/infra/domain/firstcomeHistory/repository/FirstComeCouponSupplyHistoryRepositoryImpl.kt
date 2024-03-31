package com.fcfs.coupon.app.infra.domain.firstcomeHistory.repository

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.repository.FirstComeCouponSupplyHistoryRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.utils.DateUtils.atEndOfDay
import com.fcfs.coupon.app.infra.domain.coupon.dao.CouponJpaDao
import com.fcfs.coupon.app.infra.domain.firstcome.dao.FirstComeCouponEventEntityJpaDao
import com.fcfs.coupon.app.infra.domain.firstcomeHistory.dao.FirstComeCouponSupplyHistoryEntityJpaDao
import com.fcfs.coupon.app.infra.domain.firstcomeHistory.entity.FirstComeCouponEventHistoryEntity
import com.fcfs.coupon.app.infra.domain.firstcomeHistory.entity.FirstComeCouponEventHistoryId
import com.fcfs.coupon.app.infra.domain.user.dao.UserJpaDao
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
internal class FirstComeCouponSupplyHistoryRepositoryImpl(
    val jpaDao: FirstComeCouponSupplyHistoryEntityJpaDao,
    val fcfsEventDao: FirstComeCouponEventEntityJpaDao,
    val couponDao: CouponJpaDao,
    val userDao: UserJpaDao
) : FirstComeCouponSupplyHistoryRepository {
    @Transactional
    override fun save(firstComeCouponSupplyHistory: FirstComeCouponSupplyHistory): FirstComeCouponSupplyHistory {
        return jpaDao.save(firstComeCouponSupplyHistory.toEntity()).toDomain()
    }

    override fun findByUserIdAndSupplyDateBetween(
        userId: UserId,
        start: LocalDate,
        end: LocalDate
    ): List<FirstComeCouponSupplyHistory> {
        return jpaDao.findByUserIdAndSupplyDateTimeBetween(
            userId = userId.value,
            start = start.atStartOfDay(),
            end = end.atEndOfDay()
        ).map { it.toDomain() }
    }


    private fun FirstComeCouponSupplyHistory.toEntity() = FirstComeCouponEventHistoryEntity(
        id = FirstComeCouponEventHistoryId(
            fcEventId = this.firstComeCouponEventId.value,
            userId = this.userId.value,
            couponId = this.couponId.value,
            supplyDateTime = this.supplyDateTime
        ),
        continuousReset = this.continuousReset,
        fcEvent = fcfsEventDao.findByIdOrNull(this.firstComeCouponEventId.value)
            ?: throw IllegalStateException("FirstComeCouponEventId not found"),
        user = userDao.findByIdOrNull(this.userId.value) ?: throw IllegalStateException("UserId not found"),
        coupon = couponDao.findByIdOrNull(this.couponId.value) ?: throw IllegalStateException("CouponId not found"),
        isSupplyContinuousCoupon = this.isSupplyContinuousCoupon

    )

    private fun FirstComeCouponEventHistoryEntity.toDomain() = FirstComeCouponSupplyHistory(
        firstComeCouponEventId = FirstComeCouponEventId(this.id.fcEventId),
        userId = UserId(this.id.userId),
        couponId = CouponId(this.id.couponId),
        supplyDateTime = this.id.supplyDateTime,
        continuousReset = this.continuousReset,
        isSupplyContinuousCoupon = this.isSupplyContinuousCoupon
    )
}
