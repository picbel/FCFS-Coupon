package com.fcfs.coupon.core.domain.firstcome.model

import java.time.LocalDateTime

data class FirstComeCouponSupplyHistory(
    val userId : Long,
    val couponId : Long,
    val continuousReset: Boolean,
    val generateDate: LocalDateTime
)
