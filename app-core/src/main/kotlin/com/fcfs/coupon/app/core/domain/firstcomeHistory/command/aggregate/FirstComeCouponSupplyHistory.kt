package com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period


/**
 * 선착순 쿠폰 이벤트의 이력
 */
data class FirstComeCouponSupplyHistory(
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
     * user Id
     * 식별자로 활용된다
     */
    val userId: UserId,
    /**
     * coupon id
     * 식별자로 활용된다
     */
    val couponId: CouponId,
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

        if (firstComeCouponEventId != other.firstComeCouponEventId) return false
        if (date != other.date) return false
        if (userId != other.userId) return false
        if (couponId != other.couponId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstComeCouponEventId.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + couponId.hashCode()
        return result
    }
}

object FirstComeCouponSupplyHistoriesExtendService {


    /**
     * 특정 날짜에 쿠폰이 발급되었는지 확인합니다.
     */
    fun Collection<FirstComeCouponSupplyHistory>.isAppliedByDate(userId: UserId, date: LocalDate): Boolean {
        return this.any { it.date == date && it.userId == userId }
    }

    fun Collection<FirstComeCouponSupplyHistory>.isTodayApplied(userId: UserId): Boolean {
        return this.isAppliedByDate(userId, LocalDate.now())
    }

    /**
     * 오늘을 기준으로 유저가 연속으로 쿠폰을 받은 일수를 카운트합니다.
     */
    fun Collection<FirstComeCouponSupplyHistory>.countNowConsecutiveCouponDays(userId: UserId): Long {
        this.sortedByDescending { it.date }.run {
            return countConsecutiveCouponDays(userId, LocalDate.now())
        }
    }

    // 가장 최근날짜부터 카운트 합니다
    private fun Collection<FirstComeCouponSupplyHistory>.countConsecutiveCouponDays(
        userId: UserId,
        baseDate: LocalDate
    ): Long {
        var count = 0L
        var lastDate: LocalDate = baseDate.plusDays(1) // 마지막날을 기준으로 하기위해 1일을 더합니다.
        this.forEach {
            if (it.date.isAfter(baseDate)) {
                return@forEach
            }
            if (it.userId == (userId) && isDateConsecutive(it.date, lastDate)) {
                lastDate = it.date
                ++count
                if (it.continuousReset) {
                    return count
                }
            } else {
                return count
            }
        }
        return count
    }

    private fun isDateConsecutive(now: LocalDate, lastDate: LocalDate): Boolean {
        return Period.between(now, lastDate).days == 1 // 1일차이가 나면 연속입니다.
    }

    /**
     * 현재 연속 쿠폰 발급대상자인지 확인합니다.
     * recordSupplyCouponHistory를 통하여 오늘까지의 쿠폰을 발급후 호출해야합니다.
     */
    fun Collection<FirstComeCouponSupplyHistory>.isConsecutiveCouponEligible(userId: UserId): Boolean {
        return when (countNowConsecutiveCouponDays(userId)) {
            3L -> true
            5L -> true
            7L -> true
            else -> false
        }
    }

}

