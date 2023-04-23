package com.fcfs.coupon.testcase.medium.core.domain.firstcome.repository

import com.fcfs.coupon.core.domain.coupon.Coupon
import com.fcfs.coupon.core.domain.coupon.repository.CouponRepository
import com.fcfs.coupon.core.domain.firstcome.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.core.domain.user.User
import com.fcfs.coupon.core.domain.user.repository.UserRepository
import com.fcfs.coupon.testcase.medium.MediumTestSuite
import com.fcfs.coupon.testutils.factory.CouponFactory.randomCoupon
import com.fcfs.coupon.testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import com.fcfs.coupon.testutils.factory.UserFactory.randomUser
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

@Disabled // 2023-04-23
@Suppress("NonAsciiCharacters", "className") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class FirstComeCouponEventRepositorySpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: FirstComeCouponEventRepository

    @Autowired
    private lateinit var couponRepo: CouponRepository

    @Autowired
    private lateinit var userRepo: UserRepository

    private lateinit var users : List<User>

    private lateinit var defaultCoupons : Coupon

    private lateinit var specialCoupon: Coupon

    @BeforeEach
    fun setUp() {
        users = (0..10).map { userRepo.save(randomUser()) }
        defaultCoupons = couponRepo.save(randomCoupon(discountAmount = BigDecimal(5_000)))
        specialCoupon = couponRepo.save(randomCoupon(discountAmount = BigDecimal(20_000)))
    }
    @Test
    fun `선착순 이벤트를 저장합니다`() {
        //given
        val event = randomFirstComeCouponEvent(
            defaultCouponId = defaultCoupons.id!!,
            specialCouponId = specialCoupon.id!!,
        )
        //when
        val save = sut.save(event)
        //then
        val find = sut.getById(save.id)
        save.id shouldBe event.id
        find shouldBe save
        find.defaultCouponId shouldBe save.defaultCouponId
        find.specialCouponId shouldBe save.specialCouponId

    }

    @Test
    fun `선착순 이벤트를 수정합니다`() {
        //given
        val event = randomFirstComeCouponEvent(
            defaultCouponId = defaultCoupons.id!!,
            specialCouponId = specialCoupon.id!!,
        )
        sut.save(event)
        val user = users.random()
        event.recordTodaySupplyCouponHistory(user.id!!, defaultCoupons.id!!)
        //when
        val modify = sut.save(event)
        //then
        val find = sut.getById(event.id)
        find.id shouldBe modify.id
        find shouldBe modify
        find.isTodayApplied(user.id!!) shouldBe true
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
