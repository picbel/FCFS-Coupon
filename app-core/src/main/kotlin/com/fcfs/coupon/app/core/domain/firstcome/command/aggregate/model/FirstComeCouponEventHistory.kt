package com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.model

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.time.LocalDate
import java.time.LocalDateTime


@Deprecated("FirstComeCouponSupplyHistory.kt로 이관")
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
    val supplyHistory: List<DeprecatedFirstComeCouponSupplyHistory>
) {

    fun supplyCoupon(
        userId: UserId,
        couponId: CouponId,
        continuousReset: Boolean,
        now: LocalDateTime = LocalDateTime.now() // instant로 변경 할까...
    ): FirstComeCouponEventHistory {
        return copy(
            supplyHistory = supplyHistory + DeprecatedFirstComeCouponSupplyHistory(
                userId = userId,
                couponId = couponId,
                continuousReset = continuousReset,
                supplyDateTime = now
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
