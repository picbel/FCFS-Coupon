package testcase.small.domain.firstcome

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import testutils.factory.FirstComeCouponEventFactory
import testutils.factory.FirstComeCouponEventFactory.setUpFirstComeCouponEvent
import java.time.LocalDate
import java.time.LocalDateTime

@Suppress("NonAsciiCharacters", "ClassName") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
internal class FirstComeCouponEventSpec {

    private val userId = UserId(1L)
    private val couponId = CouponId(1L)

    @Nested
    inner class `이벤트 기간을 검사합니다` {
        @Test
        fun `유효한 이벤트입니다`() {
            //given
            val date = LocalDate.now()
            val sut: FirstComeCouponEvent = FirstComeCouponEventFactory.randomFirstComeCouponEvent(
                startDate = date.minusDays(1),
                endDate = date.plusDays(1),
            )

            //when
            val result = sut.isValid()

            //then
            result shouldBe true
        }

        @Test
        fun `종료된 이벤트입니다`() {
            //given
            val date = LocalDate.now()
            val sut: FirstComeCouponEvent = FirstComeCouponEventFactory.randomFirstComeCouponEvent(
                startDate = date.minusDays(10),
                endDate = date.minusDays(1),
            )

            //when
            val result = sut.isValid()

            //then
            result shouldBe false
        }

        @Test
        fun `시작하지 않는 이벤트입니다`() {
            //given
            val date = LocalDate.now()
            val sut: FirstComeCouponEvent = FirstComeCouponEventFactory.randomFirstComeCouponEvent(
                startDate = date.plusDays(1),
                endDate = date.plusDays(10),
            )

            //when
            val result = sut.isValid()

            //then
            result shouldBe false
        }

        @Test
        fun `시작날짜가 오늘입니다`() {
            //given
            val date = LocalDate.now()
            val sut: FirstComeCouponEvent = FirstComeCouponEventFactory.randomFirstComeCouponEvent(
                startDate = date,
                endDate = date.plusDays(10),
            )

            //when
            val result = sut.isValid()

            //then
            result shouldBe true
        }

        @Test
        fun `종료날짜가 오늘입니다`() {
            //given
            val date = LocalDate.now()
            val sut: FirstComeCouponEvent = FirstComeCouponEventFactory.randomFirstComeCouponEvent(
                startDate = date.minusDays(10),
                endDate = date,
            )

            //when
            val result = sut.isValid()

            //then
            result shouldBe true
        }

    }

    // TODO ApplyForFirstComeCouponEventDomainServiceSpec.kt 이관 필요
    @Nested
    inner class `쿠폰 발급이력을 기록합니다` {
        @Test
        fun `이전 선착순 응모 기록이 없습니다`() {
            //given
            val date = LocalDateTime.now()
            val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(
                createDates = 1,
                excludedCouponDates = listOf(1),
                userId = userId,
                couponId = couponId
            )

            //when
            val result = sut.recordSupplyCouponHistory(userId, couponId, date)

            //then
            val resetExpect = result.getContinuousReset(date.toLocalDate())
            assertSoftly {
                result.isAppliedByDate(userId, date.toLocalDate()) shouldBe true
                resetExpect shouldBe false
            }
        }

        @Test
        fun `이미 선착순에 든 유저는 쿠폰이 중복발급 할 수 없습니다`() {
            //given
            val date = LocalDateTime.now()
            val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(
                createDates = 1,
                excludedCouponDates = listOf(1),
                userId = userId,
                couponId = couponId
            ).recordSupplyCouponHistory(userId, couponId, date)

            //when
            val exception = assertThrows<CustomException> {
                sut.recordSupplyCouponHistory(userId, couponId, date) // secound
            }

            //then
            exception.errorCode shouldBe ErrorCode.FC_COUPON_ALREADY_APPLIED
        }

        @Test
        fun `7일 연속 선착순 유저입니다`() {
            //given
            val date = LocalDateTime.now()
            val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(
                createDates = 7,
                excludedCouponDates = listOf(7),
                userId = userId,
                couponId = couponId
            )

            //when
            val result = sut.recordSupplyCouponHistory(userId, couponId, date)

            //then 7일까지는 연속 카운트를 reset하지 않습니다
            val resetExpect = result.getContinuousReset(date.toLocalDate())
            assertSoftly {
                result.isAppliedByDate(userId, date.toLocalDate()) shouldBe true
                resetExpect shouldBe false
            }
        }

        @Test
        fun `8일 연속 선착순 유저입니다`() {
            //given
            val date = LocalDateTime.now()
            val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(
                createDates = 8,
                excludedCouponDates = listOf(8),
                userId = userId,
                couponId = couponId
            )

            //when
            val result = sut.recordSupplyCouponHistory(userId, couponId, date)

            //then 8일에 연속 카운트를 reset합니다
            val resetExpect = result.getContinuousReset(date.toLocalDate())
            assertSoftly {
                result.isAppliedByDate(userId, date.toLocalDate()) shouldBe true
                resetExpect shouldBe true
            }
        }


        @Test
        fun `3일 연속 선착순 성공 이후 2일뒤 선착순 성공 유저입니다`() {
            //given
            val date = LocalDateTime.now()
            val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(
                createDates = 5,
                excludedCouponDates = listOf(4, 5),
                userId = userId,
                couponId = couponId
            )

            //when
            val result = sut.recordSupplyCouponHistory(userId, couponId, date)

            //then 8일에 연속 카운트를 reset합니다
            val resetExpect = result.getContinuousReset(date.toLocalDate())
            assertSoftly {
                result.isAppliedByDate(userId, date.toLocalDate()) shouldBe true
                resetExpect shouldBe false
            }
        }

        @Test
        fun `6일 연속 선착순 성공 이후 2일뒤 선착순 성공 유저입니다`() {
            //given
            val date = LocalDateTime.now()
            val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(
                createDates = 8,
                excludedCouponDates = listOf(6, 8),
                userId = userId,
                couponId = couponId
            )

            //when
            val result = sut.recordSupplyCouponHistory(userId, couponId, date)

            //then 8일에 연속 카운트를 reset합니다
            val resetExpect = result.getContinuousReset(date.toLocalDate())
            assertSoftly {
                result.isAppliedByDate(userId, date.toLocalDate()) shouldBe true
                resetExpect shouldBe false
            }
        }

        private fun FirstComeCouponEvent.getContinuousReset(
            date: LocalDate?
        ) = history.first { it.date == date }.supplyHistory.first { it.userId == userId }.continuousReset
    }

}
