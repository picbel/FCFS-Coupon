package com.fcfs.coupon.app.core.domain.coupon.command.aggregate

import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.math.BigDecimal

@JvmInline
value class CouponId(val value: Long)

/**
 * root aggregate
 */
data class Coupon(
    val id: CouponId?,
    val name: String,
    val discountAmount: BigDecimal,
) {
    val couponId: CouponId
        get() = id ?: throw IllegalStateException("unidentified coupon")

    fun supply(userId: UserId): Coupon {
        TODO(userId.toString())
//        return copy(suppliedHistory = suppliedHistory + DeprecatedSuppliedCoupon(userId, false))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coupon

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

}
