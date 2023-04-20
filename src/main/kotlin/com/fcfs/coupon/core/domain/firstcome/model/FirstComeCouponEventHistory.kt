package com.fcfs.coupon.core.domain.firstcome.model

import java.time.LocalDate
import java.util.UUID

data class FirstComeCouponEventHistory(
    val firstComeCouponEventId: UUID,
    val date: LocalDate,
    val supplyHistory: List<FirstComeCouponSupplyHistory>
) {
    fun isApplied(userId: Long): Boolean {
        return supplyHistory.any { it.userId == userId }
    }

    fun isUserContinuousReset(userId: Long): Boolean {
        return supplyHistory.find { it.userId == userId  }?.continuousReset ?: false
    }
}
