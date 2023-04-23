package com.fcfs.coupon.core.domain.firstcome.model

import java.time.LocalDateTime

/**
 * 유저 에게 발급된 쿠폰 이력
 */
class FirstComeCouponSupplyHistory(
    /**
     * user Id
     * 식별자로 활용된다
     */
    val userId : Long,
    /**
     * coupon id
     * 식별자로 활용된다
     */
    val couponId : Long,
    /**
     * 연속 선착순 일자를 reset 여부
     * 해당 필드가 true일 경우 해당필드부터 1일로 계산합니다
     */
    val continuousReset: Boolean,
    /**
     * 쿠폰 발급 일자 시간
     */
    val supplyDateTime: LocalDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FirstComeCouponSupplyHistory

        if (userId != other.userId) return false
        return couponId == other.couponId
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + couponId.hashCode()
        return result
    }
}
