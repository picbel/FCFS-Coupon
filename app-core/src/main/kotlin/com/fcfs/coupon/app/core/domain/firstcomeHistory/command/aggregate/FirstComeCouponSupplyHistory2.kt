package com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.time.LocalDate
import java.time.LocalDateTime

/*
 * 2024-02-14
 * todo : FirstComeCouponSupplyHistory과 FirstComeCouponEventHistory를 병합한것
 */
/**
 * 선착순 쿠폰 이벤트의 이력
 */
data class FirstComeCouponSupplyHistory2(
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
    val userId : UserId,
    /**
     * coupon id
     * 식별자로 활용된다
     */
    val couponId : CouponId,
    /**
     * 연속 선착순 일자를 reset 여부
     * 해당 필드가 true일 경우 해당필드부터 1일로 계산합니다
     */
    val continuousReset: Boolean,
    /**
     * 쿠폰 발급 일자 시간
     */
    val supplyDateTime: LocalDateTime
)

object FirstComeCouponSupplyHistoriesExtendService {

//    // todo : FirstComeCouponSupplyHistory2를 참조하는 도메인서비스로 분리 필요
//    // #start region
//    /**
//     * 특정 날짜에 쿠폰이 발급되었는지 확인합니다.
//     */
    fun Collection<FirstComeCouponSupplyHistory2>.isAppliedByDate(userId: UserId, date: LocalDate): Boolean {
        return this.any { it.date == date && it.userId == userId}
    }

    fun Collection<FirstComeCouponSupplyHistory2>.isTodayApplied(userId: UserId): Boolean {
        return this.isAppliedByDate(userId, LocalDate.now())
    }

    /**
     * 유저가 몇일 연속 쿠폰을 발급하였는지 확인합니다.
     */
    fun Collection<FirstComeCouponSupplyHistory2>.countNowConsecutiveCouponDays(userId: UserId): Long {
        this.sortedByDescending { it.date }.run {
            return countConsecutiveCouponDays(userId, LocalDate.now())
        }
    }

    private fun Collection<FirstComeCouponSupplyHistory2>.countConsecutiveCouponDays(userId: UserId, baseDate: LocalDate): Long {
        var count = 0L
        this.forEach {
            if (it.date.isAfter(baseDate)) {
                return@forEach
            }
            if (it.userId == (userId)) {
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

    /**
     * 현재 연속 쿠폰 발급대상자인지 확인합니다.
     * recordSupplyCouponHistory를 통하여 오늘까지의 쿠폰을 발급후 호출해야합니다.
     */
    fun Collection<FirstComeCouponSupplyHistory2>.isConsecutiveCouponEligible(userId: UserId): Boolean {
        return when (countNowConsecutiveCouponDays(userId)) {
            3L -> true
            5L -> true
            7L -> true
            else -> false
        }
    }

    private fun Collection<FirstComeCouponSupplyHistory2>.checkNextContinuousReset(userId: UserId): Boolean {
        return this.sortedByDescending { it.date }
            .countConsecutiveCouponDays(userId, LocalDate.now().minusDays(1)) == 7L
    }

}

