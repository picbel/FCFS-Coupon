package com.fcfs.coupon.testcase.small.core.domain.firstcome

import com.fcfs.coupon.core.domain.firstcome.FirstComeCouponEvent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDate

@Disabled
@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명에 한글을 사용합니다.
class FirstComeCouponEventSpec {

    private val testUserId = 1L

    @Test
    fun `유저의 이벤트 연속 응모 횟수를 조회합니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 10)
        //when
        val count = sut.countConsecutiveCouponDays()
        //then
        count shouldBe 10
    }

    @Test
    fun `3일 연속 선착순 유저는 연속 쿠폰 대상자입니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 3)
        //when
        val expect = sut.isConsecutiveCouponEligible()
        //then
        expect shouldBe true
    }

    @Test
    fun `5일 연속 선착순 유저는 연속 쿠폰 대상자입니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 5)
        //when
        val expect = sut.isConsecutiveCouponEligible()
        //then
        expect shouldBe true
    }

    @Test
    fun `7일 연속 선착순 유저는 연속 쿠폰 대상자입니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 7)
        //when
        val expect = sut.isConsecutiveCouponEligible()
        //then
        expect shouldBe true
    }

    @Test
    fun `6일 연속 선착순 유저는 연속 쿠폰 대상자가 아닙니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 7)
        //when
        val expect = sut.isConsecutiveCouponEligible()
        //then
        expect shouldBe false
    }

    @Test
    fun `8일 연속 선착순 유저는 연속 쿠폰 대상자가 아닙니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 7)
        //when
        val expect = sut.isConsecutiveCouponEligible()
        //then
        expect shouldBe false
    }

    @Test
    fun `10일 연속 선착순 유저는 연속 쿠폰 대상자입니다`() {
        //given
        val sut: FirstComeCouponEvent = setUpFirstComeCouponEvent(createDates = 7)
        //when
        val expect = sut.isConsecutiveCouponEligible()
        //then 7일차에 연속카운트 reset이후 3일이 되는 시점이라 연속 쿠폰 대상자입니다.
        expect shouldBe false
    }

    /**
     * createDates 만큼 진행된 FirstComeCouponEvent를 생성합니다.
     */
    private fun setUpFirstComeCouponEvent(createDates: Int): FirstComeCouponEvent {
        TODO()
    }
}
