package testutils.factory

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.model.SuppliedCoupon
import com.github.javafaker.Faker
import java.math.BigDecimal
import java.math.RoundingMode

object CouponFactory {
    private val faker: Faker = Faker()

    fun randomCoupon(
        id: CouponId? = null,
        name: String = faker.commerce().promotionCode().take(20),
        discountAmount: BigDecimal = BigDecimal(faker.commerce().price()).setScale(2, RoundingMode.HALF_UP),
        suppliedHistory: List<SuppliedCoupon> = mutableListOf(),
    ): Coupon {
        return Coupon(
            id, name, discountAmount, suppliedHistory
        )
    }
}