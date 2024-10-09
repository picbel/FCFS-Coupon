package com.fcfs.coupon.app.core.domain.user.query.readmodel

import com.fcfs.coupon.app.core.commons.Slice
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.math.BigDecimal
import java.time.LocalDateTime

data class UserCouponHistory(
    val id: UserId,
    val start: LocalDateTime,
    val end: LocalDateTime,
    override val content: List<CouponHistory>,
    override val nextCursor: LocalDateTime?,
    override val size: Int
)  : Slice<LocalDateTime, CouponHistory>

data class CouponHistory(
    val couponId: CouponId,
    val isUsed: Boolean,
    val name: String,
    val discountAmount: BigDecimal,
    val suppliedAt : LocalDateTime,
    val usedAt : LocalDateTime?
)
