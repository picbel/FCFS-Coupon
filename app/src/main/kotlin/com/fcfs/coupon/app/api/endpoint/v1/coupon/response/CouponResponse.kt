package com.fcfs.coupon.app.api.endpoint.v1.coupon.response

import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.CouponReadModel
import java.math.BigDecimal

data class CouponResponse(
    val id: Long,
    val name: String,
    val discountAmount: BigDecimal,
) {

    companion object {
        fun from(
            src: CouponReadModel
        ): CouponResponse {
            return CouponResponse(
                id = src.id.value,
                name = src.name,
                discountAmount = src.discountAmount,
            )
        }
    }

}
