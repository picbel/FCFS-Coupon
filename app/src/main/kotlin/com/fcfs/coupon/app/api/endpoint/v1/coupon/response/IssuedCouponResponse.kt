package com.fcfs.coupon.app.api.endpoint.v1.coupon.response

import com.fcfs.coupon.app.api.common.response.SliceResponse
import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCoupon
import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCouponHistory
import java.math.BigDecimal
import java.time.LocalDateTime

data class IssuedCouponResponse(
    val couponId: Long,
    val name: String,
    val discountAmount: BigDecimal,
    val start: LocalDateTime,
    val end: LocalDateTime,
    override val content: List<IssuedCouponHistoryResponse>,
    override val nextCursor: LocalDateTime?,
    override val size: Int

) : SliceResponse<LocalDateTime, IssuedCouponHistoryResponse> {
    companion object {
        fun from(src: IssuedCoupon) = with(src) {
            IssuedCouponResponse(
                couponId = couponId.value,
                name = name,
                discountAmount = discountAmount,
                start = start,
                end = end,
                content = history.map { IssuedCouponHistoryResponse.from(it) },
                nextCursor = nextCursor,
                size = size
            )
        }
    }
}

data class IssuedCouponHistoryResponse(
    val userId: Long,
    val isUsed: Boolean,
    val suppliedAt: LocalDateTime,
    val usedAt: LocalDateTime?
) {
    companion object {
        fun from(src: IssuedCouponHistory) = IssuedCouponHistoryResponse(
            userId = src.userId.value,
            isUsed = src.isUsed,
            suppliedAt = src.suppliedAt,
            usedAt = src.usedAt
        )
    }
}
