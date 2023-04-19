package com.fcfs.coupon.core.domain.firstcome

import com.fcfs.coupon.core.domain.firstcome.model.FirstComeCouponEventHistory
import java.time.LocalDate
import java.util.UUID

/**
 * root aggregate
 */
data class FirstComeCouponEvent(
    val id: UUID,
    val name: String,
    val description: String,
    val limitCount: Long,
    val specialLimitCount: Long,
    val history: List<FirstComeCouponEventHistory>,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
