package com.fcfs.coupon.testcase.medium.core.domain.coupon.repository

import com.fcfs.coupon.core.domain.coupon.Coupon
import com.fcfs.coupon.core.domain.coupon.model.SuppliedCoupon
import com.fcfs.coupon.core.domain.coupon.repository.CouponRepository
import com.fcfs.coupon.core.domain.user.User
import com.fcfs.coupon.core.domain.user.repository.UserRepository
import com.fcfs.coupon.testcase.medium.MediumTestSuite
import com.fcfs.coupon.testutils.factory.CouponFactory.randomCoupon
import com.fcfs.coupon.testutils.factory.UserFactory.randomUser
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class CouponRepositorySpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: CouponRepository

    @Autowired
    private lateinit var userRepo: UserRepository

    private lateinit var user: User
    private lateinit var user2: User

    /**
     * 테스트를 위한 초기화 작업을 수행합니다.
     * user를 생성합니다.
     */
    @BeforeEach
    fun setUp() {
        user = userRepo.save(randomUser())
        user2 = userRepo.save(randomUser())
    }

    @Test
    fun `Coupon을 저장합니다`() {
        //given
        val coupon: Coupon = createCoupon()
            .supply(user2.id!!)
        //when
        val save = sut.save(coupon)
        //then
        flushAndClear() // 쿼리 카운트를 위해 영속성 컨텍스트를 초기화합니다.
        val find = sut.getById(save.id!!)
        find shouldBe save
        find.discountAmount shouldBe save.discountAmount
    }

    @Test
    fun `Coupon을 수정합니다`() {
        //given
        val coupon: Coupon = createCoupon()
        sut.save(coupon)
        val update = coupon.supply(user2.id!!)
        //when
        val save = sut.save(update)
        //then
        val find = sut.getById(save.id!!)
        find shouldBe save
        find.suppliedHistory.size shouldBe 2 // createCoupon()에서 생성된 user 1개, supply로 user2가 추가되어 2개
    }

    /**
     * Coupon을 생성합니다.
     * user를 suppliedHistory에 추가합니다.
     */
    private fun createCoupon() = randomCoupon(
        suppliedHistory = listOf(
            SuppliedCoupon(
                userId = user.id!!,
                isUsed = false
            )
        )
    )
}