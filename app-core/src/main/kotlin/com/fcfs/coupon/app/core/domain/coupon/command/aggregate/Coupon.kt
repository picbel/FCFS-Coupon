package com.fcfs.coupon.app.core.domain.coupon.command.aggregate

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.model.SuppliedCoupon
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
    // 분리 필요
    val suppliedHistory: List<SuppliedCoupon>,
) {
    val couponId: CouponId
        get() = id ?: throw IllegalStateException("unidentified coupon")

    fun supply(userId: UserId): Coupon {
        return copy(suppliedHistory = suppliedHistory + SuppliedCoupon(userId, false))
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

    override fun toString(): String {
        return "Coupon(id=$id, name='$name', discountAmount=$discountAmount, suppliedHistory=$suppliedHistory)"
    }

}
