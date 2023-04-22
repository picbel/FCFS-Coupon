package com.fcfs.coupon.core.domain.coupon.model


/**
 * 지급된 쿠폰
 */
data class SuppliedCoupon(
    val userId: Long,
    val isUsed: Boolean,
)
