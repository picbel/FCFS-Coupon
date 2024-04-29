package com.fcfs.coupon.app.api.endpoint.v1.coupon.response

import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCoupon

data class IssuedCouponResponse(

) {
    companion object {
        fun from(src : IssuedCoupon) = IssuedCouponResponse(

        )
    }
}
