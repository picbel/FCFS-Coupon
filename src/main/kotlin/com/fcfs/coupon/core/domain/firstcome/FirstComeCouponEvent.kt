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
    val history: MutableList<FirstComeCouponEventHistory>,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    /**
     * 쿠폰 발급 이력을 기록합니다.
     */
    fun recordSupplyCouponHistory(
        userId: Long,
        couponId: Long,
    ): Boolean {
        TODO()
    }

    /**
     * 몇유저가 몇일 연속 쿠폰을 발급하였는지 확인합니다.
     */
    fun countConsecutiveCouponDays(): Long {
        TODO()
    }

    /**
     * 연속 쿠폰 발급이 가능한지 확인합니다.
     */
    fun isConsecutiveCouponEligible(): Boolean {
        TODO()
    }
}
