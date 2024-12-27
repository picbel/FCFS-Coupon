package com.fcfs.coupon.app.core.domain.coupon.query.readmodel

import com.fcfs.coupon.app.core.common.Slice
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 발급된 쿠폰 내역
 *
 * coupon domain과 user domain의 기반하여 조회됩니다
 * 해당 모델은 query 모델로 사용됩니다.
 * 따라서 해당 모델로 save같은 command를 수행하지 않습니다.
 */
data class IssuedCoupon(
    val couponId: CouponId,
    val name: String,
    val discountAmount: BigDecimal,
    val start: LocalDateTime,
    val end: LocalDateTime,
    override val content: List<IssuedCouponHistory>,
    override val nextCursor: LocalDateTime?,
    override val size: Int
) : Slice<LocalDateTime, IssuedCouponHistory> {
    val history: List<IssuedCouponHistory> = content
}

data class IssuedCouponHistory(
    val userId: UserId,
    val isUsed: Boolean,
    val suppliedAt: LocalDateTime,
    val usedAt: LocalDateTime?
)
