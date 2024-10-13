package com.fcfs.coupon.app.api.endpoint.v1.user

import com.fcfs.coupon.app.api.endpoint.v1.ApiPath
import com.fcfs.coupon.app.api.endpoint.v1.user.request.FindSuppliedCouponFilterRequest
import com.fcfs.coupon.app.api.endpoint.v1.user.response.UserCouponHistoryResponse
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.domain.user.query.usecase.FindUserUseCase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

interface UserController {

    @GetMapping(ApiPath.USER_COUPON_HISTORY)
    fun findSuppliedCouponHistory(
        @ModelAttribute @Valid
        filter: FindSuppliedCouponFilterRequest,
        @RequestHeader("user-id")
        userId: Long,
    ): UserCouponHistoryResponse
}

@RestController
internal class UserControllerImpl(
    private val userUseCase: FindUserUseCase
) : UserController {
    override fun findSuppliedCouponHistory(
        filter: FindSuppliedCouponFilterRequest,
        userId: Long
    ): UserCouponHistoryResponse {
        return userUseCase.findSuppliedCouponHistory(UserId(userId), filter.toMessage()).run {
            UserCouponHistoryResponse.from(this)
        }
    }
}
