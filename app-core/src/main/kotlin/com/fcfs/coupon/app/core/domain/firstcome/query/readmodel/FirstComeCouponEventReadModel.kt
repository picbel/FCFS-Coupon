package com.fcfs.coupon.app.core.domain.firstcome.query.readmodel

import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.CouponReadModel
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import java.time.LocalDate

data class FirstComeCouponEventReadModel(
    val id: FirstComeCouponEventId,
    val name: String,
    val description: String,
    val limitCount: Long,
    val specialLimitCount: Long,
    val defaultCouponId: CouponReadModel,
    val specialCouponId: CouponReadModel,
    val consecutiveCouponId: CouponReadModel,
    val startDate: LocalDate,
    val endDate: LocalDate,
)


