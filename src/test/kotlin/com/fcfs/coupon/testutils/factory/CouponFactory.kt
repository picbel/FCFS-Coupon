package com.fcfs.coupon.testutils.factory

import com.fcfs.coupon.core.domain.coupon.Coupon
import com.fcfs.coupon.core.domain.coupon.model.SuppliedCoupon
import com.github.javafaker.Faker
import java.math.BigDecimal
import java.math.RoundingMode

object CouponFactory {
    private val faker: Faker = Faker()

    fun randomCoupon(
        id: Long? = null,
        name: String = faker.commerce().promotionCode().take(20),
        discountAmount: BigDecimal = BigDecimal(faker.commerce().price()).setScale(2, RoundingMode.HALF_UP),
        suppliedHistory: List<SuppliedCoupon> = mutableListOf(),
    ): Coupon {
        return Coupon(
            id, name, discountAmount, suppliedHistory
        )
    }
}