package com.fcfs.coupon.app.core.domain.firstcome

import com.fcfs.coupon.app.core.domain.coupon.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.model.FirstComeCouponEventHistory
import com.fcfs.coupon.app.core.domain.user.UserId
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
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
    val id: FirstComeCouponEventId,
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
    val history: List<FirstComeCouponEventHistory>,
    val defaultCouponId: CouponId,
    val specialCouponId: CouponId,
    val consecutiveCouponId: CouponId,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {

    /**
     * 특정 날짜에 쿠폰이 발급되었는지 확인합니다.
     */
    fun isAppliedByDate(userId: UserId, date: LocalDate): Boolean {
        return history.find { it.date == date }?.isApplied(userId) ?: false
    }

    fun isTodayApplied(userId: UserId): Boolean {
        return isAppliedByDate(userId, LocalDate.now())
    }

    /**
     * 쿠폰 발급 이력을 기록합니다.
     */
    fun recordSupplyCouponHistory(
        userId: UserId,
        couponId: CouponId,
        date: LocalDate,
    ): FirstComeCouponEvent {
        assertSupplyCoupon(couponId)
        return copy(
            history = history.map { history ->
                if (history.date == date) {
                    if (history.isApplied(userId)) {
                        throw CustomException(ErrorCode.FC_COUPON_ALREADY_APPLIED)
                    } else {
                        history.supplyCoupon(userId, couponId, checkNextContinuousReset(userId))
                    }
                } else {
                    history
                }
            }
        )
    }

    fun recordTodaySupplyCouponHistory(
        userId: UserId,
        couponId: CouponId,
    ): FirstComeCouponEvent {
        return recordSupplyCouponHistory(userId, couponId, LocalDate.now())
    }

    private fun assertSupplyCoupon(couponId: CouponId) {
        if (couponId != defaultCouponId && couponId != specialCouponId) {
           throw CustomException(ErrorCode.FC_COUPON_MATCH_ERROR)
        }
    }

    /**
     * 유저가 몇일 연속 쿠폰을 발급하였는지 확인합니다.
     */
    fun countNowConsecutiveCouponDays(userId: UserId): Long {
        history.sortedByDescending { it.date }.run {
            return countConsecutiveCouponDays(userId, LocalDate.now())
        }
    }

    private fun List<FirstComeCouponEventHistory>.countConsecutiveCouponDays(userId: UserId, baseDate: LocalDate): Long {
        var count = 0L
        this.forEach {
            if (it.date.isAfter(baseDate)) {
                return@forEach
            }
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

    /**
     * 현재 연속 쿠폰 발급대상자인지 확인합니다.
     * recordSupplyCouponHistory를 통하여 오늘까지의 쿠폰을 발급후 호출해야합니다.
     */
    fun isConsecutiveCouponEligible(userId: UserId): Boolean {
        return when (countNowConsecutiveCouponDays(userId)) {
            3L -> true
            5L -> true
            7L -> true
            else -> false
        }
    }

    fun isValid(): Boolean {
        val today = LocalDate.now()
        return today >= startDate && today <= endDate
    }

    fun isNotValid(): Boolean {
        return !isValid()
    }

    private fun checkNextContinuousReset(userId: UserId): Boolean {
        return history.sortedByDescending { it.date }
            .countConsecutiveCouponDays(userId, LocalDate.now().minusDays(1)) == 7L
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
