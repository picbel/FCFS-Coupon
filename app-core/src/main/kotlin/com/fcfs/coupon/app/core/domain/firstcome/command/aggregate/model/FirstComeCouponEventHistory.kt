package com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.model

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.time.LocalDate
import java.time.LocalDateTime


/**
 * FirstComeCouponEvent의 이력
 *
 * todo : FirstComeCouponSupplyHistory2로 이관하고 해당 객체를 삭제
 */
data class FirstComeCouponEventHistory(
    /**
     * firstComeCouponEvent의 id
     * 해당 객체의 식별자로 활용된다
     */
    val firstComeCouponEventId: FirstComeCouponEventId,
    /**
     * 날짜
     * 해당 객체의 식별자로 활용된다
     */
    val date: LocalDate,
    /**
     * 쿠폰 발급 이력 // todo 애는 분리가 필요하여보임
     */
    val supplyHistory: List<FirstComeCouponSupplyHistory>
) {

    fun isApplied(userId: UserId): Boolean {
        return supplyHistory.any { it.userId == userId }
    }

    fun isUserContinuousReset(userId: UserId): Boolean {
        return supplyHistory.find { it.userId == userId }?.continuousReset ?: false
    }

    fun supplyCoupon(userId: UserId, couponId: CouponId, continuousReset: Boolean): FirstComeCouponEventHistory {
        return copy(
            supplyHistory = supplyHistory + FirstComeCouponSupplyHistory(
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

        other as FirstComeCouponEventHistory

        if (firstComeCouponEventId != other.firstComeCouponEventId) return false
        return date == other.date
    }

    override fun hashCode(): Int {
        var result = firstComeCouponEventId.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}
