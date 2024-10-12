package com.fcfs.coupon.app.core.domain.user.query.usecase

import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.domain.user.query.readmodel.UserCouponHistory
import com.fcfs.coupon.app.core.domain.user.query.repository.UserFinder
import com.fcfs.coupon.app.core.domain.user.query.usecase.message.FindSuppliedCouponFilter
import org.springframework.stereotype.Service

interface FindUserUseCase {
    fun findSuppliedCouponHistory(userId: UserId, filter: FindSuppliedCouponFilter) : UserCouponHistory
}

@Service
internal class FindUserUseCaseImpl(
    private val userFinder: UserFinder
) : FindUserUseCase {
    override fun findSuppliedCouponHistory(userId: UserId, filter: FindSuppliedCouponFilter): UserCouponHistory {
        return userFinder.findSuppliedCouponHistory(userId, filter)
    }
}
