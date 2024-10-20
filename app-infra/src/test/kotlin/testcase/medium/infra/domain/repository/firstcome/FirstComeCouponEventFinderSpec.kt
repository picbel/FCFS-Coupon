package testcase.medium.infra.domain.repository.firstcome

import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.domain.firstcome.query.repository.FirstComeCouponEventFinder
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import testcase.medium.infra.domain.repository.MediumTestSuite
import testutils.factory.CouponFactory.randomCoupon
import testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime


internal class FirstComeCouponEventFinderSpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: FirstComeCouponEventFinder
    @Autowired
    private lateinit var fcRepo: FirstComeCouponEventRepository

    @Autowired
    private lateinit var couponRepo: CouponRepository

    private val expectedId = FirstComeCouponEventId.newId()

    /**
     * 현재 날짜 기준으로
     * 종료된 이벤트 1개
     * 진행중인 이벤트 1개
     * 시작 1일전 이벤트 1개
     */
    @BeforeEach
    fun setUp() {
        val now = LocalDate.now()
        val default = couponRepo.save(randomCoupon(discountAmount = BigDecimal(5_000)))
        val special = couponRepo.save(randomCoupon(discountAmount = BigDecimal(20_000)))
        val consecutive = couponRepo.save(randomCoupon(discountAmount = BigDecimal(50_000)))
        // 종료된 이벤트
        fcRepo.save(
            randomFirstComeCouponEvent(
                defaultCouponId = default.id!!,
                specialCouponId = special.id!!,
                consecutiveCouponId = consecutive.id!!,
                startDate = now.minusDays(10L),
                endDate = now.minusDays(5L)
            )
        )

        // 진행중인 이벤트
        fcRepo.save(
            randomFirstComeCouponEvent(
                id = expectedId,
                defaultCouponId = default.id!!,
                specialCouponId = special.id!!,
                consecutiveCouponId = consecutive.id!!,
                startDate = now.minusDays(10L),
                endDate = now.plusDays(5L)
            )
        )

        // 시작 1일전 이벤트
        fcRepo.save(
            randomFirstComeCouponEvent(
                defaultCouponId = default.id!!,
                specialCouponId = special.id!!,
                consecutiveCouponId = consecutive.id!!,
                startDate = now.plusDays(1L),
                endDate = now.plusDays(10L)
            )
        )

    }


    @Test
    fun `진행중인 선착순 이벤트를 조회합니다`() {
        //given
        val now = LocalDateTime.now()

        //when
        val result = sut.findInProgressFirstComeCouponEvent(now)

        //then
        assertSoftly {
            result.size shouldBe 1
            result.first().id shouldBe expectedId
        }
    }

}
