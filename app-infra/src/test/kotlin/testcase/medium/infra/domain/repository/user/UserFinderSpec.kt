package testcase.medium.infra.domain.repository.user

import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.aggregate.model.SuppliedCoupon
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import com.fcfs.coupon.app.core.domain.user.query.readmodel.CouponHistory
import com.fcfs.coupon.app.core.domain.user.query.repository.UserFinder
import com.fcfs.coupon.app.core.domain.user.query.usecase.message.FindSuppliedCouponFilter
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import testcase.medium.infra.domain.repository.MediumTestSuite
import testutils.factory.CouponFactory
import testutils.factory.UserFactory
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
internal class UserFinderSpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: UserFinder

    @Autowired
    private lateinit var userRepo: UserRepository

    @Autowired
    private lateinit var couponRepo: CouponRepository

    private lateinit var tester: User

    /**
     * 2명의 유저가 10일간 쿠폰을 10장씩 매일 오후 1시에 쿠폰을 발급받았습니다
     */
    @BeforeEach
    fun setUp() {
        val now = LocalDateTime.now()
        val coupon = couponRepo.save(CouponFactory.randomCoupon())
        val users = listOf(
            userRepo.save(UserFactory.randomUser()).also { tester = it },
            userRepo.save(UserFactory.randomUser())
        )
        val baseSuppliedAt = now.truncatedTo(ChronoUnit.HOURS).withHour(13)
        // 10일간
        users.forEach {
            it.copy(
                suppliedCoupons = (1..10).reversed().map { day ->
                    SuppliedCoupon(
                        couponId = coupon.couponId,
                        isUsed = false,
                        suppliedAt = baseSuppliedAt.minusDays(day.toLong()),
                        usedAt = null
                    )
                }
            ).run {
                userRepo.save(this)
            }
        }
    }


    @Test
    fun `유저가 최근 7일간의 쿠폰내역을 조회합니다`() {
        //given
        val filter = FindSuppliedCouponFilter(
            size = 10,
            cursor = null,
            startSuppliedAt = LocalDateTime.now().minusDays(7),
            endSuppliedAt = LocalDateTime.now(),
        )
        //when
        val result = sut.findSuppliedCouponHistory(tester.userId, filter)

        //then
        assertSoftly {
            result.content.size shouldBe 7
            result.id shouldBe tester.userId
            result.content.all {
                it.suppliedAt.isAfter(filter.startSuppliedAt) && it.suppliedAt.isBefore(filter.endSuppliedAt)
            } shouldBe true
            result.hasNext shouldBe false
        }
    }


    @Test
    fun `유저가 최근 7일간의 쿠폰내역을 size 5로 조회합니다`() {
        //given
        val filter = FindSuppliedCouponFilter(
            size = 5,
            cursor = null,
            startSuppliedAt = LocalDateTime.now().minusDays(7),
            endSuppliedAt = LocalDateTime.now(),
        )
        //when
        val result = sut.findSuppliedCouponHistory(tester.userId, filter)

        //then
        assertSoftly {
            result.content.size shouldBe 5
            result.id shouldBe tester.userId
            result.content.all {
                it.suppliedAt.isAfter(filter.startSuppliedAt) && it.suppliedAt.isBefore(filter.endSuppliedAt)
            } shouldBe true
            result.hasNext shouldBe true
            result.nextCursor shouldNotBe null
        }
    }

    @Test
    fun `유저가 최근 7일간의 쿠폰내역을 size 3으로 next가 없을때까지 조회합니다`() {
        //given
        var filter = FindSuppliedCouponFilter(
            size = 3,
            cursor = null,
            startSuppliedAt = LocalDateTime.now().minusDays(7),
            endSuppliedAt = LocalDateTime.now(),
        )

        //when
        val content = mutableListOf<CouponHistory>()
        var result = sut.findSuppliedCouponHistory(tester.userId, filter)
        content.addAll(result.content)
        filter = filter.copy(cursor = result.nextCursor)
        while (result.hasNext) {
            result = sut.findSuppliedCouponHistory(tester.userId, filter)
            filter = filter.copy(cursor = result.nextCursor)
            content.addAll(result.content)
        }

        //then
        assertSoftly {
            content.size shouldBe 7
            content.all {
                it.suppliedAt.isAfter(filter.startSuppliedAt) && it.suppliedAt.isBefore(filter.endSuppliedAt)
            } shouldBe true
        }

    }
}
