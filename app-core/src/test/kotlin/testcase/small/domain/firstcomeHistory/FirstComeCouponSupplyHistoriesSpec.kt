package testcase.small.domain.firstcomeHistory

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistoriesExtendService.countNowConsecutiveCouponDays
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory2
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import testutils.factory.FirstComeCouponEventFactory
import java.time.LocalDate

@Suppress("NonAsciiCharacters", "ClassName")
internal class FirstComeCouponSupplyHistoriesSpec {

    private val userId = UserId(1L)
    private val couponId = CouponId(1L)

    // 테스트 케이스 통과 시키기 given 변형 필요
    @Nested
    inner class `이벤트 연속 응모 횟수를 조회합니다` {
        @Test
        fun `10일 연속 선착순 성공 유저입니다`() {
            //given
            val sut = setUpHistory(
                createDates = 10,
                userId = userId,
                couponId = couponId
            )

            //when
            val count = sut.countNowConsecutiveCouponDays(userId)

            //then 10일간 연속 응모 하여도 7일째에 초기화되기 때문에 3입니다.
            count shouldBe 3
        }

        @Test
        fun `20일 연속 선착순 성공 유저입니다`() {
            //given
            val sut = setUpHistory(
                    createDates = 20,
                    userId = userId,
                    couponId = couponId
                )

            //when
            val count = sut.countNowConsecutiveCouponDays(userId)

            //then 20일간 연속 응모 하여도 매 7일째에 초기화되기 때문에 6입니다.
            count shouldBe 6
        }

        @Test
        fun `3일 연속 선착순 성공 이후 다음날 연속 응모 하지 못한 유저입니다`() {
            //given
            val sut = setUpHistory(
                createDates = 4,
                excludedCouponDates = listOf(4),
                userId = userId,
                couponId = couponId
            )

            //when
            val count = sut.countNowConsecutiveCouponDays(userId)

            //then
            count shouldBe 0
        }

        @Test
        fun `3일 연속 선착순 성공 이후 2일뒤 선착순 응모한 유저입니다`() {
            //given
            val sut: FirstComeCouponEvent = FirstComeCouponEventFactory.setUpFirstComeCouponEvent(
                createDates = 5,
                excludedCouponDates = listOf(4),
                userId = userId,
                couponId = couponId
            )

            //when
            val count = sut.countNowConsecutiveCouponDays(userId)

            //then
            count shouldBe 1
        }

        @Test
        fun `6일까지 연속 선착순 성공 이후 2일뒤 선착순 응모한 유저입니다`() {

        }
    }

    private fun setUpHistory(
        createDates: Long,
        userId: UserId,
        couponId: CouponId,
        excludedCouponDates: List<Long> = listOf(),
    ): List<FirstComeCouponSupplyHistory2> {
        return (0 until createDates).reversed().mapNotNull { i ->
            if (excludedCouponDates.contains(createDates - i)) {
                null
            } else {
                val date = LocalDate.now().minusDays(i)
                FirstComeCouponSupplyHistory2(
                    firstComeCouponEventId = FirstComeCouponEventId.newId(),
                    date = date,
                    userId = userId,
                    couponId = couponId,
                    continuousReset = (createDates - i) % 7 == 1L, // 8일마다 카운트를 reset합니다.
                    supplyDateTime = date.atStartOfDay()
                )
            }
        }
    }
}
