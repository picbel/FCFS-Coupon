package com.fcfs.coupon.app.infra.domain.firstcome.repository

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.CouponReadModel
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcome.query.readmodel.FirstComeCouponEventReadModel
import com.fcfs.coupon.app.core.domain.firstcome.query.repository.FirstComeCouponEventFinder
import com.fcfs.coupon.app.infra.domain.coupon.entity.CouponEntity
import com.fcfs.coupon.app.infra.domain.firstcome.dao.FirstComeCouponEventEntityJpaDao
import com.fcfs.coupon.app.infra.domain.firstcome.entity.FirstComeCouponEventEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
internal class FirstComeCouponEventFinderImpl(
    private val fcJpaDao: FirstComeCouponEventEntityJpaDao
) : FirstComeCouponEventFinder {

    override fun findInProgressFirstComeCouponEvent(
        now: LocalDateTime
    ): List<FirstComeCouponEventReadModel> {
        val nowDate = now.toLocalDate()
        return fcJpaDao.findAllInProgressEvents(nowDate).map { it.toReadModel() }
    }

    private fun FirstComeCouponEventEntity.toReadModel(): FirstComeCouponEventReadModel {
        return FirstComeCouponEventReadModel(
            id = FirstComeCouponEventId(eventId),
            name = name,
            description = description,
            limitCount = limitCount,
            specialLimitCount = specialLimitCount,
            defaultCouponId = defaultCoupon.toReadModel(),
            specialCouponId = specialCoupon.toReadModel(),
            consecutiveCouponId = consecutiveCoupon.toReadModel(),
            startDate = startDate,
            endDate = endDate,
        )
    }

    private fun CouponEntity.toReadModel(): CouponReadModel {
        return CouponReadModel(
            id = CouponId(couponId ?: throw IllegalStateException("coupon id is null")),
            name = name,
            discountAmount = discountAmount,
        )
    }
}
