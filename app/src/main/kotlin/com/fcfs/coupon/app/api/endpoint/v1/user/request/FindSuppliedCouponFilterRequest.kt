package com.fcfs.coupon.app.api.endpoint.v1.user.request

import com.fcfs.coupon.app.core.domain.user.query.usecase.message.FindSuppliedCouponFilter
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class FindSuppliedCouponFilterRequest(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val cursor: LocalDateTime? = null,
    @Min(1) @Max(100) // 최대 한번에 100개까지 조회 가능
    val size: Int,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val startSuppliedAt: LocalDateTime,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val endSuppliedAt: LocalDateTime,
) {
    fun toMessage() = FindSuppliedCouponFilter(
        cursor,
        size,
        startSuppliedAt,
        endSuppliedAt
    )
}
