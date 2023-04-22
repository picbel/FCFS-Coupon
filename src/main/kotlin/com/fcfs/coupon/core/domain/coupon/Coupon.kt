package com.fcfs.coupon.core.domain.coupon

import com.fcfs.coupon.core.domain.coupon.model.SuppliedCoupon
import java.math.BigDecimal

/**
 * root aggregate
 */
class Coupon(
    val id: Long?,
    val name: String,
    val discountAmount: BigDecimal,
    suppliedHistory: List<SuppliedCoupon>,
) {
    private val _suppliedHistory: MutableList<SuppliedCoupon> = suppliedHistory.toMutableList()
    val suppliedHistory: List<SuppliedCoupon>
        get() = _suppliedHistory.toList()

    fun supply(userId: Long): Coupon {
        _suppliedHistory.add(SuppliedCoupon(userId, false))
        return this
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
