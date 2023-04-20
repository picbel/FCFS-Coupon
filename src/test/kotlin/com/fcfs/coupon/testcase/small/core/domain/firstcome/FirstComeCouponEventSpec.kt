package com.fcfs.coupon.testcase.small.core.domain.firstcome

import com.fcfs.coupon.core.domain.firstcome.FirstComeCouponEvent
import com.fcfs.coupon.core.domain.firstcome.model.FirstComeCouponEventHistory
import com.fcfs.coupon.core.domain.firstcome.model.FirstComeCouponSupplyHistory
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명에 한글을 사용합니다.
class FirstComeCouponEventSpec {

    private val userId = 1L
    private val couponId = 1L

    @Test
    fun `10일 연속 선착순 유저의 이벤트 연속 응모 횟수를 조회합니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 10)

        //when
        val count = sut.countConsecutiveCouponDays(userId)

        //then 10일간 연속 응모 하여도 7일째에 초기화되기 때문에 3입니다.
        count shouldBe 3
    }

    @Test
    fun `20일 연속 선착순 유저의 이벤트 연속 응모 횟수를 조회합니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 20)

        //when
        val count = sut.countConsecutiveCouponDays(userId)

        //then 20일간 연속 응모 하여도 매 7일째에 초기화되기 때문에 6입니다.
        count shouldBe 6
    }

    @Test
    fun `3일 연속 선착순 이후 다음날 연속 응모 하지 못하였습니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 3).apply {
            this.history.add(
                FirstComeCouponEventHistory(
                    firstComeCouponEventId = id,
                    date = LocalDate.now().plusDays(1),
                    supplyHistory = listOf() // 다음날 응모 기록이 없습니다
                )
            )
        }

        //when
        val count = sut.countConsecutiveCouponDays(userId)

        //then
        count shouldBe 0
    }

    @Test
    fun `3일 연속 선착순 이후 2일뒤 선착순 응모한 유저의 이벤트 연속 응모 횟수를 조회합니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 3).apply {
            this.history.add(
                FirstComeCouponEventHistory(
                    firstComeCouponEventId = id,
                    date = LocalDate.now().plusDays(1),
                    supplyHistory = listOf() // 다음날 응모 기록이 없습니다
                )
            )
            this.history.add(
                FirstComeCouponEventHistory(
                    firstComeCouponEventId = id,
                    date = LocalDate.now().plusDays(2),
                    supplyHistory = listOf(FirstComeCouponSupplyHistory(
                        userId = userId,
                        couponId = couponId,
                        continuousReset = false,
                        generateDate = LocalDate.now().plusDays(2).atStartOfDay()
                    ))
                )
            )
        }

        //when
        val count = sut.countConsecutiveCouponDays(userId)

        //then
        count shouldBe 1
    }

    @Test
    fun `3일 연속 선착순 유저는 연속 쿠폰 대상자입니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 3)

        //when
        val expect = sut.isConsecutiveCouponEligible(userId)

        //then
        expect shouldBe true
    }

    @Test
    fun `5일 연속 선착순 유저는 연속 쿠폰 대상자입니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 5)
        //when
        val expect = sut.isConsecutiveCouponEligible(userId)

        //then
        expect shouldBe true
    }

    @Test
    fun `7일 연속 선착순 유저는 연속 쿠폰 대상자입니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 7)

        //when
        val expect = sut.isConsecutiveCouponEligible(userId)

        //then
        expect shouldBe true
    }

    @Test
    fun `6일 연속 선착순 유저는 연속 쿠폰 대상자가 아닙니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 6)

        //when
        val expect = sut.isConsecutiveCouponEligible(userId)

        //then
        expect shouldBe false
    }

    @Test
    fun `8일 연속 선착순 유저는 연속 쿠폰 대상자가 아닙니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 8)

        //when
        val expect = sut.isConsecutiveCouponEligible(userId)

        //then
        expect shouldBe false
    }

    @Test
    fun `10일 연속 선착순 유저는 연속 쿠폰 대상자입니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 10)

        //when
        val expect = sut.isConsecutiveCouponEligible(userId)

        //then 7일차에 연속카운트 reset이후 3일이 되는 시점이라 연속 쿠폰 대상자입니다.
        expect shouldBe true
    }

    /**
     * createDates 만큼 진행된 FirstComeCouponEvent를 생성합니다.
     */
    private fun setUpFirstComeCouponEvent(createDates: Long): FirstComeCouponEvent {
        return FirstComeCouponEvent(
            id = UUID.randomUUID(),
            name = "test",
            description = "test",
            limitCount = 100,
            specialLimitCount = 10,
            history = mutableListOf(),
            startDate = LocalDate.now().minusDays(createDates),
            endDate = LocalDate.now().plusDays(createDates),
        ).apply {
            (0 until createDates).reversed().map { i ->
                val date = LocalDate.now().minusDays(i)
                FirstComeCouponEventHistory(
                    firstComeCouponEventId = id,
                    date = date,
                    supplyHistory = listOf(
                        FirstComeCouponSupplyHistory(
                            userId = userId,
                            couponId = couponId,
                            continuousReset = (createDates - i) % 7 == 1L, // 8일마다 카운트를 reset합니다.
                            generateDate = date.atStartOfDay()
                        )
                    )
                ).also { history.add(it) }
            }
        }
    }
}
