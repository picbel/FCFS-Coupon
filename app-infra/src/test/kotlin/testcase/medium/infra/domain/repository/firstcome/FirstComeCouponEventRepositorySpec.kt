package testcase.medium.infra.domain.repository.firstcome

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.dto.FirstComeCouponEventEntryResult
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import com.fcfs.coupon.app.infra.domain.firstcome.dao.FirstComeCouponEventRedisDao
import com.fcfs.coupon.app.infra.domain.firstcome.model.FirstComeCoupon
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import testcase.medium.infra.domain.repository.MediumTestSuite
import testutils.concurrency.ConcurrencyTestUtils.parallelExecute
import testutils.factory.CouponFactory.randomCoupon
import testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import testutils.factory.UserFactory.randomUser
import java.math.BigDecimal
import java.time.LocalDate


@Suppress("NonAsciiCharacters", "className") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class FirstComeCouponEventRepositorySpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: FirstComeCouponEventRepository

    @Autowired
    private lateinit var couponRepo: CouponRepository

    @Autowired
    private lateinit var userRepo: UserRepository

    @Autowired
    private lateinit var redisDao: FirstComeCouponEventRedisDao

    private lateinit var user: User

    private lateinit var defaultCoupons: Coupon

    private lateinit var specialCoupon: Coupon

    private lateinit var consecutiveCoupon: Coupon

    @BeforeEach
    fun setUp() {
        user = userRepo.save(randomUser())
        defaultCoupons = couponRepo.save(randomCoupon(discountAmount = BigDecimal(5_000)))
        specialCoupon = couponRepo.save(randomCoupon(discountAmount = BigDecimal(20_000)))
        consecutiveCoupon = couponRepo.save(randomCoupon(discountAmount = BigDecimal(50_000)))
    }

    @Test
    fun `선착순 이벤트를 저장합니다`() {
        //given
        val event = randomFirstComeCouponEvent(
            defaultCouponId = defaultCoupons.id!!,
            specialCouponId = specialCoupon.id!!,
            consecutiveCouponId = consecutiveCoupon.id!!,
        )
        //when
        val save = sut.save(event)
        //then
        flushAndClear()
        val find = sut.getById(save.id)
        assertSoftly {
            save.id shouldBe event.id
            find shouldBe save
            find.defaultCouponId shouldBe save.defaultCouponId
            find.specialCouponId shouldBe save.specialCouponId
        }

    }

    @Test
    fun `선착순 이벤트를 수정합니다`() {
        //given
        val event = randomFirstComeCouponEvent(
            defaultCouponId = defaultCoupons.id!!,
            specialCouponId = specialCoupon.id!!,
            consecutiveCouponId = consecutiveCoupon.id!!,
        )
        sut.save(event)
        val update = event.recordTodaySupplyCouponHistory(user.id!!, defaultCoupons.id!!)
        //when
        val modify = sut.save(update)
        //then
        flushAndClear()
        val find = sut.getById(event.id)
        assertSoftly {
            find.id shouldBe modify.id
            find shouldBe modify
            find.isTodayApplied(user.id!!) shouldBe true
        }
    }

    @Nested
    inner class `선착순 10명 이벤트에` {

        private lateinit var event: FirstComeCouponEvent

        @BeforeEach
        fun redisSetUp() {
            event = randomFirstComeCouponEvent(
                defaultCouponId = defaultCoupons.id!!,
                specialCouponId = specialCoupon.id!!,
                limitCount = 10,
                specialLimitCount = 1
            )
            val firstComeCoupons = firstComeCouponSetUp(event)
            redisDao.saveAll(firstComeCoupons)
        }


        @Test
        fun `첫번째 시도입니다`() {
            //given //when
            val result = sut.applyForFirstComeCouponEvent(event.id)
            //then
            assertSoftly {
                result.order shouldBe 1
                result.isIncludedInFirstCome shouldBe true
            }
        }

        @Test
        fun `10번째 시도입니다`() {
            //given
            repeat(9) {
                sut.applyForFirstComeCouponEvent(event.id)
            }
            //when
            val result = sut.applyForFirstComeCouponEvent(event.id)
            //then
            assertSoftly {
                result.order shouldBe 10
                result.isIncludedInFirstCome shouldBe true
            }
        }

        @Test
        fun `11번째 시도입니다`() {
            //given
            repeat(10) {
                sut.applyForFirstComeCouponEvent(event.id)
            }
            //when
            val result = sut.applyForFirstComeCouponEvent(event.id)
            //then
            assertSoftly {
                result.order shouldBe null
                result.isIncludedInFirstCome shouldBe false
            }
        }

        /*
         * 코틀린 코루틴을 쓰는 방법도 있으나 실제 캐리어 쓰레드에서 실행되는 상황을 가정하기 위해 ExecutorService를 사용합니다.
         */
        @Test
        fun `11명이 동시에 시도합니다`() {
            //given
            val callCount = 11
            //when
            val futures = parallelExecute(callCount) {
                sut.applyForFirstComeCouponEvent(event.id)
            }
            //then
            val results: List<FirstComeCouponEventEntryResult> = futures.map { it.get() }
            assertSoftly {
                results.size shouldBe callCount
                results.count { it.isIncludedInFirstCome } shouldBe event.limitCount
                results.count { it.couponId == defaultCoupons.id } shouldBe (event.limitCount - event.specialLimitCount)
                results.count { it.couponId == specialCoupon.id } shouldBe event.specialLimitCount
            }
        }

        /**
         * event의 limitCount 만큼의 FirstComeCoupon을 생성합니다
         * fcCoupons의 갯수는 event.limitCount와 같습니다.
         * specialLimitCount 만큼의 FirstComeCoupon를 변경합니다
         */
        private fun firstComeCouponSetUp(event: FirstComeCouponEvent): List<FirstComeCoupon> {
            val fcCoupons: MutableList<FirstComeCoupon> = mutableListOf()
            repeat(event.limitCount.toInt()) {
                fcCoupons.add(
                    FirstComeCoupon(
                        eventId = event.id.value,
                        date = LocalDate.now(),
                        order = (it + 1).toLong(),
                        couponId = defaultCoupons.id!!.value
                    )
                )
            }
            while (fcCoupons.count { it.couponId == specialCoupon.id?.value } < event.specialLimitCount) {
                val random = fcCoupons.random()
                fcCoupons.remove(random)
                fcCoupons.add(random.copy(couponId = specialCoupon.id!!.value))
            }
            return fcCoupons.sortedBy { it.order }
        }
    }
}
