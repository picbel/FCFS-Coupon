package com.fcfs.coupon.app.core.domain.firstcome.command.aggregate

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import java.time.LocalDate
import java.util.*

@JvmInline
value class FirstComeCouponEventId(val value: UUID) {
    companion object {
        fun newId() = FirstComeCouponEventId(UUID.randomUUID())
    }
}

/**
 * root aggregate
 */
data class FirstComeCouponEvent(
    /**
     * 이벤트 ID
     */
    val id: FirstComeCouponEventId,
    /**
     * 이벤트 이름
     */
    val name: String,
    /**
     * 이벤트 설명
     */
    val description: String,
    /**
     * 선착순 마감 수량
     */
    val limitCount: Long,
    /**
     * 특별 쿠폰 지급 수량
     */
    val specialLimitCount: Long,
    /**
     * 기본 쿠폰
     */
    val defaultCouponId: CouponId,
    /**
     * 특별 쿠폰
     */
    val specialCouponId: CouponId,
    /**
     * 연속 선착순 달성시 지급 쿠폰
     */
    val consecutiveCouponId: CouponId,
    /**
     * 이벤트 시작일
     */
    val startDate: LocalDate,
    /**
     * 이벤트 종료일
     */
    val endDate: LocalDate,
) {

    fun isValid(): Boolean {
        val today = LocalDate.now()
        return today >= startDate && today <= endDate
    }

    fun isNotValid(): Boolean {
        return !isValid()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FirstComeCouponEvent

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}
