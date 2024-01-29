package com.fcfs.coupon.app.core.domain.firstcome.message

import java.util.*

data class ApplyFirstComeCouponEventMessage(
    val userId: Long,
    val firstComeCouponEventId: UUID
)