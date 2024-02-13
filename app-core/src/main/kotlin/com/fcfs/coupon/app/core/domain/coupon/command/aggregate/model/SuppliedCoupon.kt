package com.fcfs.coupon.app.core.domain.coupon.command.aggregate.model

import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId


/**
 * 지급된 쿠폰
 */
data class SuppliedCoupon(
    val userId: UserId,
    val isUsed: Boolean,
)
