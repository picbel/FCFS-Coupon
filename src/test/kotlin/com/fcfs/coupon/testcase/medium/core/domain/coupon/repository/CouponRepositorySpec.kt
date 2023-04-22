package com.fcfs.coupon.testcase.medium.core.domain.coupon.repository

import com.fcfs.coupon.core.domain.coupon.repository.CouponRepository
import com.fcfs.coupon.core.domain.user.repository.UserRepository
import com.fcfs.coupon.testcase.medium.MediumTestSuite
import com.github.javafaker.Faker
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class CouponRepositorySpec : MediumTestSuite() {

    val faker: Faker = Faker()

    @Autowired
    private lateinit var sut: CouponRepository

    @Test
    fun `Coupon을 저장합니다`() {

    }

    @Test
    fun `Coupon을 수정합니다`() {

    }
}