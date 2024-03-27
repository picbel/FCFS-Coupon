package testcase.small.domain.user

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testutils.factory.CouponFactory.randomCoupon
import testutils.factory.UserFactory.randomUser


@Suppress("NonAsciiCharacters", "ClassName") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class UserSpec {

    @Test
    fun `유저에게 쿠폰을 발급합니다`() {
        //given
        val coupon = randomCoupon(id = CouponId(1))
        var user = randomUser()
        //when
        user = user.supplyCoupon(coupon.couponId)
        //then
        assertSoftly {
            user.suppliedCoupons.size shouldBe 1
            user.suppliedCoupons.first().couponId shouldBe coupon.id
            user.suppliedCoupons.first().isUsed shouldBe false
        }
    }

    @Test
    fun `유저가 쿠폰을 사용하였습니다`() {
        //given
        val coupon = randomCoupon(id = CouponId(1))
        var user = randomUser()
            .supplyCoupon(coupon.couponId)
            .supplyCoupon(randomCoupon(id = CouponId(2)).couponId)
        //when
        user = user.useCoupon(coupon.couponId)
        //then
        assertSoftly {
            user.suppliedCoupons.size shouldBe 2
            user.suppliedCoupons.first { it.couponId == coupon.couponId }.isUsed shouldBe true
            user.suppliedCoupons.first { it.couponId != coupon.couponId }.isUsed shouldBe false
        }
    }

}
