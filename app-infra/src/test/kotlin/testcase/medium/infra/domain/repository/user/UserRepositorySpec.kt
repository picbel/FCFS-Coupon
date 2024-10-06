package testcase.medium.infra.domain.repository.user

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import testcase.medium.infra.domain.repository.MediumTestSuite
import testutils.factory.CouponFactory
import testutils.factory.UserFactory.randomUser


@Suppress("NonAsciiCharacters","ClassName") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class UserRepositorySpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: UserRepository


    @Autowired
    private lateinit var couponRepo: CouponRepository

    @Test
    fun `User를 저장합니다`() {
        //given
        val user = randomUser()
        //when
        val save = sut.save(user)
        //then
        flushAndClear()
        val find = sut.getById(save.id!!)
        assertSoftly {
            find shouldBe save
            find.toString() shouldBe save.toString() // 안쪽 데이터에 List가 없기에 가능한 비교입니다
        }
    }

    @Nested
    inner class `User를 수정합니다` {

        @Test
        fun `User에게 쿠폰을 발급하고 저장합니다`() {
            val user = randomUser()
            val save = sut.save(user)
            val coupon: Coupon = couponRepo.save(CouponFactory.randomCoupon())
            flushAndClear()
            val modify = randomUser(id = save.id).supplyCoupon(coupon.couponId)
            //when
            val update = sut.save(modify)
            //then
            flushAndClear()
            val find = sut.getById(save.id!!)
            assertSoftly {
                find shouldBe update
                find.toString() shouldBe update.toString() // 안쪽 데이터에 List가 없기에 가능한 비교입니다
                save.toString() shouldNotBe find.toString()
                modify.suppliedCoupons.size shouldBe find.suppliedCoupons.size
            }
        }

        @Test
        fun `이미 저장된 User의 suppliedCoupons을 비운 상태로 저장합니다`() {
            val coupon: Coupon = couponRepo.save(CouponFactory.randomCoupon())
            val user =  randomUser()
            val save = sut.save(user).run {
                sut.save(this.supplyCoupon(coupon.couponId))
            }
            flushAndClear()
            val modify = save.copy(suppliedCoupons = emptyList())
            //when
            val update = sut.save(modify)

            //then
            flushAndClear()
            val find = sut.getById(save.id!!)
            assertSoftly {
                find shouldBe update
                find.toString() shouldBe update.toString()
                save.toString() shouldNotBe find.toString()
                modify.suppliedCoupons.size shouldBe find.suppliedCoupons.size
            }
        }

    }


}
