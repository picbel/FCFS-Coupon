package testcase.large.api.endpoint.v1.coupon

import com.fcfs.coupon.app.api.endpoint.v1.ApiPath
import com.fcfs.coupon.app.api.endpoint.v1.coupon.request.IssuedCouponFilterRequest
import com.fcfs.coupon.app.api.endpoint.v1.coupon.response.IssuedCouponResponse
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.coupon.query.repository.CouponFinder
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.repository.FirstComeCouponSupplyHistoryRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.model.SuppliedCoupon
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.restassured.RestAssured.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import testcase.large.LargeTestSuite
import testutils.factory.CouponFactory.randomCoupon
import testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import testutils.factory.UserFactory.randomUser
import java.time.LocalDateTime

@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
internal class GetCouponApiSpec : LargeTestSuite() {

    @Autowired
    private lateinit var eventRepo: FirstComeCouponEventRepository

    @Autowired
    private lateinit var couponRepo: CouponRepository

    @Autowired
    private lateinit var userRepo: UserRepository

    @Autowired
    private lateinit var firstComeCouponSupplyHistoryRepo: FirstComeCouponSupplyHistoryRepository

    @Autowired
    private lateinit var couponFinder: CouponFinder

    private val now: LocalDateTime = LocalDateTime.now()

    private lateinit var coupon: Coupon

    /**
     * 3명의 유저가 10일간 쿠폰을 발급받았습니다.
     */
    @BeforeEach
    fun setUp() {
        // 10일간 10명의 유저가 이벤트에 응모하였습니다 총 100개의 데이터가 생성됩니다
        val users = (1..10).map { userRepo.save(randomUser()) }
        coupon = couponRepo.save(randomCoupon())
        val event = randomFirstComeCouponEvent(
            defaultCouponId = coupon.couponId,
            consecutiveCouponId = couponRepo.save(randomCoupon()).couponId,
            specialCouponId = couponRepo.save(randomCoupon()).couponId,
        ).run { eventRepo.save(this) }
        users.forEach { user ->
            var newUser = user
            repeat(10) {
                firstComeCouponSupplyHistoryRepo.save(
                    FirstComeCouponSupplyHistory(
                        firstComeCouponEventId = event.id,
                        userId = user.userId,
                        couponId = event.defaultCouponId,
                        supplyDateTime = now.minusDays(it.toLong()),
                        continuousReset = false,
                        isSupplyContinuousCoupon = false
                    )
                )
                newUser = user.copy(
                    suppliedCoupons = newUser.suppliedCoupons + SuppliedCoupon(
                        couponId = event.defaultCouponId,
                        suppliedAt = now.minusDays(it.toLong()),
                        isUsed = false,
                        usedAt = null
                    )
                )
            }
            userRepo.save(newUser)
        }
    }

    @Test
    fun `쿠폰 발급 이력을 조회합니다`() {
        // given
        val filter = IssuedCouponFilterRequest(
            cursor = LocalDateTime.now(),
            size = 10,
            start = LocalDateTime.now().minusDays(10),
            end = LocalDateTime.now(),
        )
        // when
        val res: IssuedCouponResponse = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
                    .params(filter.toParams())
            .get(ApiPath.COUPON_ISSUE_ID.replace("{id}", coupon.couponId.value.toString()))
            .then()
            .extract()
            .response()
            .expectSuccess()
        couponFinder.findAllByCouponId(filter.toMessage(coupon.couponId))

        // then
        assertSoftly {
            res.content.size shouldBe 10
            res.nextCursor shouldNotBe null
        }

    }

    private fun IssuedCouponFilterRequest.toParams() = mutableMapOf<String, String?>().apply {
        set("cursor", cursor?.toString())
        set("size", this@toParams.size.toString())
        set("start", start.toString())
        set("end", end.toString())
    }
}
