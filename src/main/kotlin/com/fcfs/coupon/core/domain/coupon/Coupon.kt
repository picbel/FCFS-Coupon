package com.fcfs.coupon.core.domain.coupon

import com.fcfs.coupon.core.domain.coupon.model.SuppliedCoupon
import java.math.BigDecimal

/**
 * root aggregate
 */
data class Coupon(
    val id: Long?,
    val name: String,
    val discountAmount: BigDecimal,
    val suppliedHistory: List<SuppliedCoupon>,
) {
    val couponId: Long
        get() = id ?: throw IllegalStateException("unidentified coupon")

    fun supply(userId: Long): Coupon {
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
