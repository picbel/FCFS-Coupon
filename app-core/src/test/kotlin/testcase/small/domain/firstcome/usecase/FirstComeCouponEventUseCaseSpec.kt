package testcase.small.domain.firstcome.usecase

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.message.ApplyFirstComeCouponEventMessage
import com.fcfs.coupon.app.core.domain.firstcome.command.usecase.FirstComeCouponEventUseCase
import com.fcfs.coupon.app.core.domain.firstcome.command.usecase.FirstComeCouponEventUseCaseImpl
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.repository.FirstComeCouponSupplyHistoryRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import testutils.factory.CouponFactory.randomCoupon
import testutils.factory.FirstComeCouponEventFactory
import testutils.factory.FirstComeCouponEventFactory.setUpFirstComeCouponEvent
import testutils.factory.FirstComeCouponSupplyHistoryFactory.firstComeCouponSupplyHistoriesSetUp
import testutils.factory.UserFactory.randomUser
import testutils.fake.repository.FakeCouponRepository
import testutils.fake.repository.FakeFirstComeCouponEventRepository
import testutils.fake.repository.FakeFirstComeCouponSupplyHistoryRepository
import testutils.fake.repository.FakeUserRepository


@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class FirstComeCouponEventUseCaseSpec {
    /*
     * usecase단을 테스트 할때 mocking을 해야하는지 혹은 fakeDao를 구현해야하는지 고민이 됩니다.
     * fakeDao의 경우 mocking보다 값을 셋팅하는 부분에 있어 편리함이 있어 fakeRepo를 구현하여 테스트를 진행하였습니다
     */
    private lateinit var sut: FirstComeCouponEventUseCase

    private lateinit var fcRepo: FakeFirstComeCouponEventRepository
    private lateinit var fcHistoryRepo: FirstComeCouponSupplyHistoryRepository
    private lateinit var userRepo: UserRepository
    private lateinit var couponRepo: CouponRepository

    @BeforeEach
    fun setUp() {
        fcRepo = FakeFirstComeCouponEventRepository()
        fcHistoryRepo = FakeFirstComeCouponSupplyHistoryRepository()
        userRepo = FakeUserRepository()
        couponRepo = FakeCouponRepository()
        sut = FirstComeCouponEventUseCaseImpl(
            fcRepo, fcHistoryRepo,userRepo, couponRepo
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
        val user = userRepo.save(randomUser(id = UserId(11)))
        val coupon = couponRepo.save(randomCoupon())
        val event = FirstComeCouponEventFactory.randomFirstComeCouponEvent(
            defaultCouponId = coupon.couponId,
            limitCount = 10,
            specialLimitCount = 1,
        )
        fcRepo.save(event)
        fcRepo.setApplyForFirstComeCouponEventFlag(false)
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
        val coupons = (1..3).map { couponRepo.save(randomCoupon(id = CouponId(it.toLong()))) }
        val event = setUpFirstComeCouponEvent(
            createDates = 3,
            excludedCouponDates = listOf(3),
            userId = user.userId,
            couponId = coupons.first { it.id?.value == 1L }.id!!,
            limitCount = 10,
            specialLimitCount = 1
        )
        fcRepo.save(event)
        // 미지막날을 제외하고 3일간의 선착순 이벤트를 진행합니다
        firstComeCouponSupplyHistoriesSetUp(
            createDates = 3,
            excludedCouponDates = listOf(3),
            userId = user.userId,
            couponId = coupons.first { it.id?.value == 1L }.id!!
        ).forEach {
            fcHistoryRepo.save(it)
        }
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
