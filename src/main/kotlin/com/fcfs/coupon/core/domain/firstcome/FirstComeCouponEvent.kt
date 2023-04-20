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
    /*
    * domain의 표현상 모든 이력을 들고있는것이 좋다 생각하지만 만약 이벤트가 1년을 넘어 2..3년이 된다면 문제가 발생할 수 있다
    * 1년지속시 FirstComeCouponEventHistory의 예상데이터수는 365개이며
    * 내부의 FirstComeCouponSupplyHistory의 데이터까지 생각하면 limitCount만큼 곱한 사이즈가 예상데이터 수이다
    * 현재 제한 수량 100개기준 생각하니 예상되는 데이터수는 36500이다.
    * 이 정도 갯수는 문제가 되지 않을것이라 생각하지만 주의 관리 포인트 중에 하나이다.
    */
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
     * 유저가 몇일 연속 쿠폰을 발급하였는지 확인합니다.
     */
    fun countConsecutiveCouponDays(userId: Long): Long {
        history.sortedByDescending { it.date }.run {
            var count = 0L
            this.forEach {
                if (it.isApplied(userId)) {
                    ++count
                    if (it.isUserContinuousReset(userId)) {
                        return count
                    }
                } else {
                    return count
                }
            }
            return count
        }
    }

    /**
     * 연속 쿠폰 발급이 가능한지 확인합니다.
     */
    fun isConsecutiveCouponEligible(userId: Long): Boolean {
        return when (countConsecutiveCouponDays(userId)) {
            3L -> true
            5L -> true
            7L -> true
            else -> false
        }
    }
}
