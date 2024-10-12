package com.fcfs.coupon.app.api.endpoint.v1.user

import com.fcfs.coupon.app.api.endpoint.v1.user.request.FindSuppliedCouponFilterRequest
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.domain.user.query.readmodel.UserCouponHistory
import com.fcfs.coupon.app.core.domain.user.query.usecase.FindUserUseCase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

interface UserController {
    // TODO: 사용자의 쿠폰 발급이력 조회 api
    fun findSuppliedCouponHistory(
        @ModelAttribute @Valid
        filter: FindSuppliedCouponFilterRequest,
        @RequestHeader("user-id")
        userId: Long,
    ): UserCouponHistory
}

@RestController
internal class UserControllerImpl(
    private val userUseCase: FindUserUseCase
) : UserController {
    override fun findSuppliedCouponHistory(filter: FindSuppliedCouponFilterRequest, userId: Long): UserCouponHistory {
        return userUseCase.findSuppliedCouponHistory(UserId(userId), filter.toMessage())
    }
}
