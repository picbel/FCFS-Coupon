package com.fcfs.coupon.app.core.domain.firstcome.command.aggregate

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.model.FirstComeCouponEventHistory
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import java.time.LocalDate
import java.time.LocalDateTime
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
    *
    * 2024-02-14 command model 에서 해당 정보가 필요할까...? 발행을 따로 해야하지않을까?
    * FirstComeCouponEventHistory의 생명주기를 이제 따로 보고 분리하자
    * history를 따로 뺄 필요는 없을꺼같다
    * history 자체보단 발급이력을 별도의 생명주기로 봐야할것 같다
    *
    * todo : FirstComeCouponSupplyHistory2로 이관됨 해당 필드를 삭제해야함
    */
    @Deprecated("FirstComeCouponSupplyHistory2로 이관됨 해당 필드를 삭제해야함")
    val history: List<FirstComeCouponEventHistory>,
    val defaultCouponId: CouponId,
    val specialCouponId: CouponId,
    val consecutiveCouponId: CouponId,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {

    @Deprecated("추후 쿠폰쪽 리펙토링때 분리 예정")
    /**
     * 쿠폰 발급 이력을 기록합니다.
     */
    fun recordSupplyCouponHistory(
        userId: UserId,
        couponId: CouponId,
        date: LocalDateTime,
    ): FirstComeCouponEvent {
        assertSupplyCoupon(couponId)
        return copy(
            history = history.map { history ->
                if (history.date == date.toLocalDate()) {
                    if (history.isApplied(userId)) {
                        throw CustomException(ErrorCode.FC_COUPON_ALREADY_APPLIED)
                    } else {
                        history.supplyCoupon(userId, couponId, checkNextContinuousReset(userId), date)
                    }
                } else {
                    history
                }
            }
        )
    }


    private fun assertSupplyCoupon(couponId: CouponId) {
        if (couponId != defaultCouponId && couponId != specialCouponId) {
           throw CustomException(ErrorCode.FC_COUPON_MATCH_ERROR)
        }
    }

    // #end region

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
