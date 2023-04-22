package com.fcfs.coupon.core.domain.coupon

import com.fcfs.coupon.core.domain.coupon.model.SuppliedCoupon
import java.math.BigDecimal

/**
 * root aggregate
 */
data class Coupon(
    val id: Long?,
    val name: String,
    val discountAmount: BigDecimal,
    val suppliedHistory: List<SuppliedCoupon>,
)
