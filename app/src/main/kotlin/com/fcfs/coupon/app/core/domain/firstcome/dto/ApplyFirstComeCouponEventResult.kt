package com.fcfs.coupon.app.core.domain.firstcome.dto

import java.math.BigDecimal

data class ApplyFirstComeCouponEventResult (
    val order: Long?,
    val isIncludedInFirstCome: Boolean,
    val couponId: Long?,
    val couponName: String?,
    val couponDiscountAmount: BigDecimal?,
    val isConsecutiveCouponSupplied: Boolean,
)