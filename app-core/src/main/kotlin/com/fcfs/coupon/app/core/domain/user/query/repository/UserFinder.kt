package com.fcfs.coupon.app.core.domain.user.query.repository

import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.domain.user.query.readmodel.UserCouponHistory
import com.fcfs.coupon.app.core.domain.user.query.usecase.message.FindSuppliedCouponFilter

interface UserFinder {

    fun findSuppliedCouponHistory(userId: UserId, filter: FindSuppliedCouponFilter) : UserCouponHistory

}


