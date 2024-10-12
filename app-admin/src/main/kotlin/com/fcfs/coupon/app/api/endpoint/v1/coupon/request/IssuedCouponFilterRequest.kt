package com.fcfs.coupon.app.api.endpoint.v1.coupon.request

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.coupon.query.usecase.message.IssuedCouponFilter
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class IssuedCouponFilterRequest(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val cursor: LocalDateTime? = null,
    @Min(1) @Max(100) // 최대 한번에 100개까지 조회 가능 // 최대 한번에 100개까지 조회 가능
    val size: Int,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val start: LocalDateTime,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
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
