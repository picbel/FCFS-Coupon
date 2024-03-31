package testcase.small.domain.firstcome.usecase.service

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.usecase.service.ApplyForFirstComeCouponEventDomainService
import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import testutils.factory.FirstComeCouponSupplyHistoryFactory.firstComeCouponSupplyHistoriesSetUp
import testutils.factory.UserFactory.randomUser

@Suppress("NonAsciiCharacters", "ClassName")
internal class ApplyForFirstComeCouponEventDomainServiceSpec  {

    private lateinit var user : User
    private val couponId = CouponId(1L)
    private val fcfsEvent = randomFirstComeCouponEvent(
        defaultCouponId = couponId,
        limitCount = 10,
        specialLimitCount = 1
    )

    private val sut : ApplyForFirstComeCouponEventDomainService
        get() = object : ApplyForFirstComeCouponEventDomainService {}

    @Nested
    inner class `이벤트를 발급합니다` {

        @BeforeEach
        fun setup() {
            user = randomUser(id = UserId(1L))
        }

        @Test
        fun `이전 선착순 응모 기록이 없습니다`() {
            //given //when
            val (eventUser, result) = sut.supplyTodayFirstComeCoupon(
                fcfsEvent,
                listOf(),
                user,
                couponId,
            )

            //then
            assertSoftly {
                result.continuousReset shouldBe false
                eventUser.suppliedCoupons.size shouldBe 1
            }
        }

        @Test
        fun `이미 선착순에 든 유저는 쿠폰이 중복발급 할 수 없습니다`() {
            //given
            val histories = firstComeCouponSupplyHistoriesSetUp(
                createDates = 1,
                userId = user.userId,
                couponId = couponId
            )

            //when
            val exception = assertThrows<CustomException> {
                sut.supplyTodayFirstComeCoupon(fcfsEvent, histories, user, couponId) // secound
            }

            //then
            exception.errorCode shouldBe ErrorCode.FC_COUPON_ALREADY_APPLIED
        }

        @Test
        fun `7일 연속 선착순 유저입니다`() {
            //given
            val histories = firstComeCouponSupplyHistoriesSetUp(
                createDates = 7,
                excludedCouponDates = listOf(7),
                userId = user.userId,
                couponId = couponId
            )
            //when
            val (eventUser, result) = sut.supplyTodayFirstComeCoupon(fcfsEvent, histories, user, couponId)

            //then 7일까지는 연속 카운트를 reset하지 않습니다
            assertSoftly {
                result.continuousReset shouldBe false
                eventUser.suppliedCoupons.size shouldBe 2 // 기본 쿠폰 + 7일차 연속 쿠폰
            }
        }

        @Test
        fun `8일 연속 선착순 유저입니다`() {
            //given
            val histories = firstComeCouponSupplyHistoriesSetUp(
                createDates = 8,
                excludedCouponDates = listOf(8),
                userId = user.userId,
                couponId = couponId
            )
            //when
            val (eventUser, result) = sut.supplyTodayFirstComeCoupon(fcfsEvent, histories, user, couponId)

            //then 8일에 연속 카운트를 reset합니다
            assertSoftly {
                result.continuousReset shouldBe true
                eventUser.suppliedCoupons.size shouldBe 1 // 기본 쿠폰만 발급
            }
        }


        @Test
        fun `3일 연속 선착순 성공 이후 2일뒤 선착순 성공 유저입니다`() {
            //given
            val histories = firstComeCouponSupplyHistoriesSetUp(
                createDates = 5,
                excludedCouponDates = listOf(4, 5),
                userId = user.userId,
                couponId = couponId
            )

            //when
            val (eventUser, result) = sut.supplyTodayFirstComeCoupon(fcfsEvent, histories, user, couponId)

            //then 8일에 연속 카운트를 reset합니다
            assertSoftly {
                result.continuousReset shouldBe false
                eventUser.suppliedCoupons.size shouldBe 1 // 기본 쿠폰만 발급
            }
        }

        @Test
        fun `6일 연속 선착순 성공 이후 2일뒤 선착순 성공 유저입니다`() {
            //given
            val histories = firstComeCouponSupplyHistoriesSetUp(
                createDates = 8,
                excludedCouponDates = listOf(6, 8),
                userId = user.userId,
                couponId = couponId
            )

            //when
            val (eventUser, result) = sut.supplyTodayFirstComeCoupon(fcfsEvent, histories, user, couponId)

            //then 8일에 연속 카운트를 reset합니다
            assertSoftly {
                result.continuousReset shouldBe false
                eventUser.suppliedCoupons.size shouldBe 1 // 기본 쿠폰만 발급
            }
        }

    }
}
