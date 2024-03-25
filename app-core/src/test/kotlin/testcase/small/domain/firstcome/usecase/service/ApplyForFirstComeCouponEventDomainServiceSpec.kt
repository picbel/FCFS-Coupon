package testcase.small.domain.firstcome.usecase.service

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.usecase.service.ApplyForFirstComeCouponEventDomainService
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import testutils.factory.FirstComeCouponSupplyHistoryFactory
import testutils.factory.FirstComeCouponSupplyHistoryFactory.firstComeCouponSupplyHistoriesSetUp

@Suppress("NonAsciiCharacters", "ClassName")
internal class ApplyForFirstComeCouponEventDomainServiceSpec  {

    private val userId = UserId(1L)
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
        @Test
        fun `이전 선착순 응모 기록이 없습니다`() {
            //given //when
            val result = sut.supplyTodayFirstComeCoupon(
                fcfsEvent,
                listOf(),
                userId,
                couponId,
            )

            //then
            assertSoftly {
                result.continuousReset shouldBe false
            }
        }

        @Test
        fun `이미 선착순에 든 유저는 쿠폰이 중복발급 할 수 없습니다`() {
            //given
            val histories = FirstComeCouponSupplyHistoryFactory.firstComeCouponSupplyHistoriesSetUp(
                createDates = 1,
                userId = userId,
                couponId = couponId
            )

            //when
            val exception = assertThrows<CustomException> {
                sut.supplyTodayFirstComeCoupon(fcfsEvent, histories, userId, couponId) // secound
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
                userId = userId,
                couponId = couponId
            )
            //when
            val result = sut.supplyTodayFirstComeCoupon(fcfsEvent, histories, userId, couponId)

            //then 7일까지는 연속 카운트를 reset하지 않습니다
            assertSoftly {
                result.continuousReset shouldBe false
            }
        }

        @Test
        fun `8일 연속 선착순 유저입니다`() {
            //given
            val histories = firstComeCouponSupplyHistoriesSetUp(
                createDates = 8,
                excludedCouponDates = listOf(8),
                userId = userId,
                couponId = couponId
            )
            //when
            val result = sut.supplyTodayFirstComeCoupon(fcfsEvent, histories, userId, couponId)

            //then 8일에 연속 카운트를 reset합니다
            assertSoftly {
                result.continuousReset shouldBe true
            }
        }


        @Test
        fun `3일 연속 선착순 성공 이후 2일뒤 선착순 성공 유저입니다`() {
            //given
            val histories = firstComeCouponSupplyHistoriesSetUp(
                createDates = 5,
                excludedCouponDates = listOf(4, 5),
                userId = userId,
                couponId = couponId
            )

            //when
            val result = sut.supplyTodayFirstComeCoupon(fcfsEvent, histories, userId, couponId)

            //then 8일에 연속 카운트를 reset합니다
            assertSoftly {
                result.continuousReset shouldBe false
            }
        }

        @Test
        fun `6일 연속 선착순 성공 이후 2일뒤 선착순 성공 유저입니다`() {
            //given
            val histories = firstComeCouponSupplyHistoriesSetUp(
                createDates = 8,
                excludedCouponDates = listOf(6, 8),
                userId = userId,
                couponId = couponId
            )

            //when
            val result = sut.supplyTodayFirstComeCoupon(fcfsEvent, histories, userId, couponId)

            //then 8일에 연속 카운트를 reset합니다
            assertSoftly {
                result.continuousReset shouldBe false
            }
        }

    }
}
