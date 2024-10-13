package com.fcfs.coupon.app.api.endpoint.v1.user.response

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fcfs.coupon.app.api.common.response.SliceResponse
import com.fcfs.coupon.app.core.domain.user.query.readmodel.CouponHistory
import com.fcfs.coupon.app.core.domain.user.query.readmodel.UserCouponHistory
import java.time.LocalDateTime


@JsonSerialize
data class UserCouponHistoryResponse(
    val start: LocalDateTime,
    val end: LocalDateTime,
    override val content: List<CouponHistoryResponse>,
    override val nextCursor: LocalDateTime?,
    override val size: Int
) : SliceResponse<LocalDateTime, CouponHistoryResponse> {
    companion object {
        fun from(src: UserCouponHistory) = UserCouponHistoryResponse(
            src.start,
            src.end,
            src.content.map { CouponHistoryResponse.from(it) },
            src.nextCursor,
            src.size
        )
    }
}

@JsonSerialize
data class CouponHistoryResponse(
    val couponId: Long,
    val isUsed: Boolean,
    val name: String,
    val discountAmount: String,
    val suppliedAt: LocalDateTime,
    val usedAt: LocalDateTime?
) {
    companion object {
        fun from(src: CouponHistory) = CouponHistoryResponse(
            src.couponId.value,
            src.isUsed,
            src.name,
            src.discountAmount.toString(),
            src.suppliedAt,
            src.usedAt
        )
    }
}
