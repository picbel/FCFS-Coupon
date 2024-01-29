package com.fcfs.coupon.app.core.domain.firstcome.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * FirstComeCouponEvent의 이력
 */
data class FirstComeCouponEventHistory(
    /**
     * firstComeCouponEvent의 id
     * 해당 객체의 식별자로 활용된다
     */
    val firstComeCouponEventId: UUID,
    /**
     * 날짜
     * 해당 객체의 식별자로 활용된다
     */
    val date: LocalDate,
    /**
     * 쿠폰 발급 이력
     */
    val supplyHistory: List<com.fcfs.coupon.app.core.domain.firstcome.model.FirstComeCouponSupplyHistory>
) {

    fun isApplied(userId: Long): Boolean {
        return supplyHistory.any { it.userId == userId }
    }

    fun isUserContinuousReset(userId: Long): Boolean {
        return supplyHistory.find { it.userId == userId }?.continuousReset ?: false
    }

    fun supplyCoupon(userId: Long, couponId: Long, continuousReset: Boolean): com.fcfs.coupon.app.core.domain.firstcome.model.FirstComeCouponEventHistory {
        return copy(
            supplyHistory = supplyHistory + com.fcfs.coupon.app.core.domain.firstcome.model.FirstComeCouponSupplyHistory(
                userId = userId,
                couponId = couponId,
                continuousReset = continuousReset,
                supplyDateTime = LocalDateTime.now()
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as com.fcfs.coupon.app.core.domain.firstcome.model.FirstComeCouponEventHistory

        if (firstComeCouponEventId != other.firstComeCouponEventId) return false
        return date == other.date
    }

    override fun hashCode(): Int {
        var result = firstComeCouponEventId.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}
