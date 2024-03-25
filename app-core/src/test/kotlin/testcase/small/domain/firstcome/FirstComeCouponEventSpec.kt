package testcase.small.domain.firstcome

import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import testutils.factory.FirstComeCouponEventFactory
import java.time.LocalDate

@Suppress("NonAsciiCharacters", "ClassName") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
internal class FirstComeCouponEventSpec {
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

}
