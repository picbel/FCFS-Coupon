package com.fcfs.coupon.app.core.domain.firstcome.command.dto

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import java.math.BigDecimal

data class ApplyFirstComeCouponEventResult (
    val order: Long?,
    val isIncludedInFirstCome: Boolean,
    val couponId: CouponId?,
    val couponName: String?,
    val couponDiscountAmount: BigDecimal?,
    val isConsecutiveCouponSupplied: Boolean,
)