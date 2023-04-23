package com.fcfs.coupon.testcase.medium.core.domain.firstcome.repository

import com.fcfs.coupon.core.domain.coupon.repository.CouponRepository
import com.fcfs.coupon.core.domain.firstcome.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.core.domain.user.repository.UserRepository
import com.fcfs.coupon.testcase.medium.MediumTestSuite
import com.fcfs.coupon.testutils.factory.UserFactory
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Disabled // 2023-04-23
@Suppress("NonAsciiCharacters", "className") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class FirstComeCouponEventRepositorySpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: FirstComeCouponEventRepository

    @Autowired
    private lateinit var couponRepo: CouponRepository

    @Autowired
    private lateinit var userRepo: UserRepository

    @BeforeEach
    fun setUp() {

    }
    @Test
    fun `선착순 이벤트 요청을 저장합니다`() {
        //given

        //when

        //then
    }

    @Test
    fun `선착순 이벤트 요청을 수정합니다`() {
        //given

        //when

        //then
    }

    @Nested
    inner class `선착순 10명 이벤트 참가` {
        @Test
        fun `첫번째 유저입니다`() {
            //given

            //when

            //then
        }

        @Test
        fun `10번째 유저입니다`() {
            //given

            //when

            //then
        }

        @Test
        fun `11번째 유저입니다`() {
            //given

            //when

            //then
        }

        @Test
        fun `11명이 동시에 요청합니다`() {
            //given

            //when

            //then
        }
    }
}
