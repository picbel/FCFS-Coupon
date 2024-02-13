package com.fcfs.coupon.app.core.domain.firstcome.command.message

import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId

data class ApplyFirstComeCouponEventMessage(
    val userId: UserId,
    val firstComeCouponEventId: FirstComeCouponEventId
)