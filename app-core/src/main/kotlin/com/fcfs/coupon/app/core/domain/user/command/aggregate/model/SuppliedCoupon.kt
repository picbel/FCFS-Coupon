package com.fcfs.coupon.app.core.domain.user.command.aggregate.model

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import java.time.LocalDateTime


/**
 * 지급된 쿠폰
 */
data class SuppliedCoupon(
    val couponId: CouponId,
    val isUsed: Boolean,
    val suppliedAt : LocalDateTime,
    val usedAt : LocalDateTime?
)
