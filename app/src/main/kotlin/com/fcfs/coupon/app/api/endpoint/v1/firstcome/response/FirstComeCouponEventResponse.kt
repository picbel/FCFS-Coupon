package com.fcfs.coupon.app.api.endpoint.v1.firstcome.response

import com.fcfs.coupon.app.api.endpoint.v1.coupon.response.CouponResponse
import com.fcfs.coupon.app.core.domain.firstcome.query.readmodel.FirstComeCouponEventReadModel
import java.time.LocalDate
import java.util.*

data class FirstComeCouponEventResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val limitCount: Long,
    val specialLimitCount: Long,
    val defaultCouponId: CouponResponse,
    val specialCouponId: CouponResponse,
    val consecutiveCouponId: CouponResponse,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {

    companion object {
        fun from(
            src: FirstComeCouponEventReadModel
        ): FirstComeCouponEventResponse {
            return FirstComeCouponEventResponse(
                id = src.id.value,
                name = src.name,
                description = src.description,
                limitCount = src.limitCount,
                specialLimitCount = src.specialLimitCount,
                defaultCouponId = CouponResponse.from(src.defaultCouponId),
                specialCouponId = CouponResponse.from(src.specialCouponId),
                consecutiveCouponId = CouponResponse.from(src.consecutiveCouponId),
                startDate = src.startDate,
                endDate = src.endDate,
            )
        }
    }
}
