package com.fcfs.coupon.app.core.domain.coupon.model

import com.fcfs.coupon.app.core.domain.user.UserId


/**
 * 지급된 쿠폰
 */
data class SuppliedCoupon(
    val userId: UserId,
    val isUsed: Boolean,
)
