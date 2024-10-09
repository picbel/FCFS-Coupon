package com.fcfs.coupon.app.infra.domain.user.repository

import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.domain.user.query.readmodel.UserCouponHistory
import com.fcfs.coupon.app.core.domain.user.query.repository.UserFinder
import com.fcfs.coupon.app.core.domain.user.query.usecase.message.FindSuppliedCouponFilter
import com.fcfs.coupon.app.infra.domain.user.dao.UserJpaDao
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class UserFinderImpl(
    private val userDao: UserJpaDao,
) : UserFinder {

    @Transactional(readOnly = true)
    override fun findSuppliedCouponHistory(userId: UserId, filter: FindSuppliedCouponFilter): UserCouponHistory {
        TODO("Not yet implemented")
    }

}
