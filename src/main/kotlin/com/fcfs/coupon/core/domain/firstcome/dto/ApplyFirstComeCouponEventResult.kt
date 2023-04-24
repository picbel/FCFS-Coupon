package com.fcfs.coupon.core.domain.firstcome.dto

import java.math.BigDecimal

data class ApplyFirstComeCouponEventResult (
    val order: Long?,
    val isIncludedInFirstCome: Boolean,
    val couponName: String?,
    val couponDiscountAmount: BigDecimal?,
    val isConsecutiveCouponSupplied: Boolean,
)