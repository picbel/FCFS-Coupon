package com.fcfs.coupon.app.core.domain.coupon.query.usecase.message

import com.fcfs.coupon.app.core.common.Sliceable
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import java.time.LocalDateTime

data class IssuedCouponFilter(
    override val cursor: LocalDateTime?,
    override val size: Int,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val couponId: CouponId
) : Sliceable<LocalDateTime>
