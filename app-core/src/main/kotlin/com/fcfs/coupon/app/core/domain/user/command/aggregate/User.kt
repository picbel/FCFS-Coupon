package com.fcfs.coupon.app.core.domain.user.command.aggregate

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.user.command.aggregate.model.Gender
import com.fcfs.coupon.app.core.domain.user.command.aggregate.model.SuppliedCoupon
import java.time.LocalDate
import java.time.LocalDateTime

@JvmInline
value class UserId(val value: Long)

/**
 * root aggregate
 */
data class User(
    val id: UserId?,
    val name: String,
    val email: String,
    val phone: String,
    val birthday: LocalDate?,
    val gender: Gender?,
    val address: String?,
    /*
     * 유저별 지급된 쿠폰
     * 해당 내역은 user와 생명주기가 같다 판단되어서 다음과 같이 구현
     */
    val suppliedCoupons: List<SuppliedCoupon>
) {
    val userId: UserId
        get() = id ?: throw IllegalStateException("unidentified user")

    fun supplyCoupon(coupon: CouponId): User {
        return copy(
            suppliedCoupons = suppliedCoupons + SuppliedCoupon(
                coupon,
                isUsed = false,
                suppliedAt = LocalDateTime.now(),
                usedAt = null
            )
        )
    }

    fun useCoupon(coupon: CouponId): User {
        return copy(suppliedCoupons = suppliedCoupons.map {
            if (it.couponId == coupon) {
                it.copy(isUsed = true, usedAt = LocalDateTime.now())
            } else {
                it
            }
        })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}
