package com.fcfs.coupon.app.core.domain.firstcome.message

import com.fcfs.coupon.app.core.domain.firstcome.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.user.UserId

data class ApplyFirstComeCouponEventMessage(
    val userId: UserId,
    val firstComeCouponEventId: FirstComeCouponEventId
)