package com.fcfs.coupon.core.domain.firstcome.model

import java.time.LocalDate
import java.util.UUID

data class FirstComeCouponEventHistory(
    val firstComeCouponEventId: UUID,
    val date: LocalDate,
    val supplyHistory: List<FirstComeCouponSupplyHistory>
)
