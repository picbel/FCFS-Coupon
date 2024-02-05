package coupon.testcase.small.core.domain.firstcome.usecase

import com.fcfs.coupon.core.domain.coupon.repository.CouponRepository
import com.fcfs.coupon.core.domain.firstcome.message.ApplyFirstComeCouponEventMessage
import com.fcfs.coupon.core.domain.firstcome.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.core.domain.firstcome.usecase.FirstComeCouponEventUseCase
import com.fcfs.coupon.core.domain.firstcome.usecase.FirstComeCouponEventUseCaseImpl
import com.fcfs.coupon.core.domain.user.repository.UserRepository
import com.fcfs.coupon.testutils.factory.CouponFactory.randomCoupon
import com.fcfs.coupon.testutils.factory.FirstComeCouponEventFactory
import com.fcfs.coupon.testutils.factory.FirstComeCouponEventFactory.setUpFirstComeCouponEvent
import com.fcfs.coupon.testutils.factory.UserFactory.randomUser
import com.fcfs.coupon.testutils.fake.repository.FakeCouponRepository
import com.fcfs.coupon.testutils.fake.repository.FakeFirstComeCouponEventRepository
import com.fcfs.coupon.testutils.fake.repository.FakeUserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class FirstComeCouponEventUseCaseSpec {
    /*
     * usecase단을 테스트 할때 mocking을 해야하는지 혹은 fakeDao를 구현해야하는지 고민이 됩니다.
     * fakeDao의 경우 mocking보다 값을 셋팅하는 부분에 있어 편리함이 있어 fakeRepo를 구현하여 테스트를 진행하였습니다
     */
    private lateinit var sut: FirstComeCouponEventUseCase

    private lateinit var fcRepo: FirstComeCouponEventRepository
    private lateinit var userRepo: UserRepository
    private lateinit var couponRepo: CouponRepository

    @BeforeEach
    fun setUp() {
        fcRepo = FakeFirstComeCouponEventRepository()
        userRepo = FakeUserRepository()
        couponRepo = FakeCouponRepository()
        sut = FirstComeCouponEventUseCaseImpl(
            fcRepo, userRepo, couponRepo
        )
    }

    @Test
    fun `선착순 유저입니다`() {
        // given
        val user = userRepo.save(randomUser())
        val coupon = couponRepo.save(randomCoupon())
        val event = FirstComeCouponEventFactory.randomFirstComeCouponEvent(
            defaultCouponId = coupon.couponId,
            limitCount = 10,
            specialLimitCount = 1
        )
        fcRepo.save(event)
        val message = ApplyFirstComeCouponEventMessage(
            userId = user.userId,
            firstComeCouponEventId = event.id,
        )
        // when
        val result = sut.applyForFirstComeCouponEvent(message)
        // then 쿠폰 발급여부 확인
        assertSoftly {
            result.isIncludedInFirstCome shouldBe true
            result.isConsecutiveCouponSupplied shouldBe false
        }
    }

    @Test
    fun `유저가 선착순에 들지 못했습니다`() {
        // given
        val alreadyUser = (1..10).map { userRepo.save(randomUser()) }
        val user = userRepo.save(randomUser(id = 11))
        val coupon = couponRepo.save(randomCoupon())
        var event = FirstComeCouponEventFactory.randomFirstComeCouponEvent(
            defaultCouponId = coupon.couponId,
            limitCount = 10,
            specialLimitCount = 1,
        )
        alreadyUser.forEach { // 10명의 유저를 미리 선점 시켜버립니다
            event = event.recordTodaySupplyCouponHistory(it.userId, couponId = coupon.couponId)
        }
        fcRepo.save(event)
        val message = ApplyFirstComeCouponEventMessage(
            userId = user.userId,
            firstComeCouponEventId = event.id,
        )
        // when
        val result = sut.applyForFirstComeCouponEvent(message)
        // then 쿠폰 발급여부 확인
        assertSoftly {
            result.isIncludedInFirstCome shouldBe false
            result.isConsecutiveCouponSupplied shouldBe false
        }
    }

    @Test
    fun `3일 연속 선착순 유저입니다`() {
        // given
        val user = userRepo.save(randomUser())
        val coupons = (1..3).map { couponRepo.save(randomCoupon(id = it.toLong())) }
        val event = setUpFirstComeCouponEvent(
            createDates = 3,
            excludedCouponDates = listOf(3),
            userId = user.userId,
            couponId = coupons.first { it.id == 1L }.id!!,
            limitCount = 10,
            specialLimitCount = 1
        )
        fcRepo.save(event)
        val message = ApplyFirstComeCouponEventMessage(
            userId = user.userId,
            firstComeCouponEventId = event.id,
        )
        // when
        val result = sut.applyForFirstComeCouponEvent(message)
        // then 쿠폰 발급여부 확인
        assertSoftly {
            result.isIncludedInFirstCome shouldBe true
            result.isConsecutiveCouponSupplied shouldBe true
        }
    }

}
