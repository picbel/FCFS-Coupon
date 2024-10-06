package com.fcfs.coupon.app.api.endpoint.v1.coupon

import com.fcfs.coupon.app.api.endpoint.v1.ApiPath
import com.fcfs.coupon.app.api.endpoint.v1.coupon.request.IssuedCouponFilterRequest
import com.fcfs.coupon.app.api.endpoint.v1.coupon.response.IssuedCouponResponse
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.coupon.query.usecase.FindCouponUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

interface AdminCouponController {

    /**
     * Admin에서 사용할만한 기능이라 별도로 빼야하지만 CQRS예제를 위해 기능을 같이 둡니다
     */
    @GetMapping(ApiPath.COUPON_ISSUE_ID)
    fun findIssuedCoupon(
        @PathVariable id: Long,
        @ModelAttribute filter: IssuedCouponFilterRequest
    ): IssuedCouponResponse
}

@RestController
internal class AdminCouponControllerImpl(
    private val findCouponUseCase: FindCouponUseCase
) : AdminCouponController {

    override fun findIssuedCoupon(id: Long,filter: IssuedCouponFilterRequest): IssuedCouponResponse {
        return findCouponUseCase.findAllCouponHistory(filter.toMessage(
            couponId = CouponId(id)
        )).run {
            IssuedCouponResponse.from(this)
        }
    }

}
