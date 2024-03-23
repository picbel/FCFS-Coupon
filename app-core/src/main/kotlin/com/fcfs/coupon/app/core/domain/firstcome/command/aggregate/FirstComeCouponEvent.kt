package com.fcfs.coupon.app.core.domain.firstcome.command.aggregate

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
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
    val defaultCouponId: CouponId,
    val specialCouponId: CouponId,
    val consecutiveCouponId: CouponId,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {


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
