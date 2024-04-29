package com.fcfs.coupon.app.api.endpoint.v1.coupon

import com.fcfs.coupon.app.api.endpoint.v1.ApiPath
import com.fcfs.coupon.app.api.endpoint.v1.coupon.request.IssuedCouponFilterRequest
import com.fcfs.coupon.app.api.endpoint.v1.coupon.response.IssuedCouponResponse
import com.fcfs.coupon.app.core.domain.coupon.query.usecase.FindCouponUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute

interface GetCouponController {

    @GetMapping(ApiPath.COUPON_ISSUE)
    fun findIssuedCoupon(
        @ModelAttribute filter: IssuedCouponFilterRequest
    ): IssuedCouponResponse
}

internal class GetCouponControllerImpl(
    private val findCouponUseCase: FindCouponUseCase
) : GetCouponController {
    override fun findIssuedCoupon(filter: IssuedCouponFilterRequest): IssuedCouponResponse {
        return findCouponUseCase.findAllCouponHistory(filter.toMessage()).run {
            IssuedCouponResponse.from(this)

        }
    }

}
