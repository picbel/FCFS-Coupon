package testcase.medium.infra.domain.repository.coupon

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import testcase.medium.infra.domain.repository.MediumTestSuite
import testutils.factory.CouponFactory.randomCoupon


@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class CouponRepositorySpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: CouponRepository

    @Test
    fun `Coupon을 저장합니다`() {
        //given
        val coupon: Coupon = randomCoupon()
        //when
        val save = sut.save(coupon)
        //then
        flushAndClear() // 쿼리 카운트를 위해 영속성 컨텍스트를 초기화합니다.
        val find = sut.getById(save.id!!)
        assertSoftly {
            find shouldBe save
            find.discountAmount shouldBe save.discountAmount
            find.name shouldBe save.name
        }
    }

    @Test
    fun `Coupon을 수정합니다`() {
        //given
        val coupon: Coupon = randomCoupon()
        sut.save(coupon)
        val name = "update"
        val discountAmount = 10000.toBigDecimal()
        val update = coupon.copy(
            name = name,
            discountAmount = discountAmount
        )
        //when
        val save = sut.save(update)
        //then
        flushAndClear()
        val find = sut.getById(save.id!!)
        assertSoftly {
            find shouldBe save
            find.discountAmount shouldBe discountAmount
            find.name shouldBe name
        }
    }

}