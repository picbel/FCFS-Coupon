package testcase.small.domain.coupon

import com.github.javafaker.Faker
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testutils.factory.CouponFactory.randomCoupon

@Suppress("NonAsciiCharacters")
class CouponSpec {
    private val faker = Faker()
    @Test
    fun `유저에게 쿠폰을 발급합니다`() {
        //given
        val coupon = randomCoupon()
        val randomUser = faker.random().nextLong()
        //when
        val result = coupon.supply(randomUser)
        //then
        assertSoftly {
            result.suppliedHistory.any { it.userId == randomUser } shouldBe true
            result.suppliedHistory.find { it.userId == randomUser }?.isUsed shouldBe false
        }
    }
}
