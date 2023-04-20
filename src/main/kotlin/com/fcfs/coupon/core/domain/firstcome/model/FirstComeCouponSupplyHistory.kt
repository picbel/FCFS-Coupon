package com.fcfs.coupon.core.domain.firstcome.model

import java.time.LocalDateTime

data class FirstComeCouponSupplyHistory(
    val userId : Long,
    val couponId : Long,
    /**
     * 연속 선착순 일자를 reset합니다
     * 해당 필드가 true이 해당필드부터 1일로 계산합니다
     */
    val continuousReset: Boolean,
    val generateDate: LocalDateTime
)
