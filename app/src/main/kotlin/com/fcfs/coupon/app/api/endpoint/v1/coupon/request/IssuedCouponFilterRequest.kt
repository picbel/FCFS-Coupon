package com.fcfs.coupon.app.api.endpoint.v1.coupon.request

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.coupon.query.usecase.message.IssuedCouponFilter
import java.time.LocalDateTime

data class IssuedCouponFilterRequest(
    val cursor: LocalDateTime?,
    val size: Int,
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    fun toMessage(
        couponId: CouponId
    ) = IssuedCouponFilter(
        cursor,
        size,
        start,
        end,
        couponId
    )
}
