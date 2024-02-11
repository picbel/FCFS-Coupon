package com.fcfs.coupon.app.api.endpoint.v1.firstcome.response

import com.fcfs.coupon.app.core.domain.firstcome.dto.ApplyFirstComeCouponEventResult
import java.math.BigDecimal

data class EntryFirstComeCouponEventResponse(
    val order: Long?,
    val isSuccess: Boolean,
    val couponName: String?,
    val couponDiscountAmount: BigDecimal?,
    val isConsecutiveCouponSupplied: Boolean,
    val couponId: Long?
) {
    companion object {
        fun from(src: ApplyFirstComeCouponEventResult) : EntryFirstComeCouponEventResponse {
            return EntryFirstComeCouponEventResponse(
                order = src.order,
                isSuccess = src.isIncludedInFirstCome,
                couponName = src.couponName,
                couponDiscountAmount = src.couponDiscountAmount,
                isConsecutiveCouponSupplied = src.isConsecutiveCouponSupplied,
                couponId = src.couponId?.value
            )
        }
    }
}
