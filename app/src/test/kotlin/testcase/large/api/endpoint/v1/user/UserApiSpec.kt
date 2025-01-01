package testcase.large.api.endpoint.v1.user

import com.fcfs.coupon.app.api.endpoint.v1.ApiPath
import com.fcfs.coupon.app.api.endpoint.v1.user.request.FindSuppliedCouponFilterRequest
import com.fcfs.coupon.app.api.endpoint.v1.user.response.UserCouponHistoryResponse
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.aggregate.model.SuppliedCoupon
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured.given
import io.restassured.response.Response
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import testcase.large.LargeTestSuite
import testutils.factory.CouponFactory
import testutils.factory.UserFactory
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

internal class UserApiSpec : LargeTestSuite() {

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
                suppliedCoupons = (0..9).reversed().map { day ->
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
    fun `유저가 발급받은 쿠폰 히스토리를 조회합니다`() {
        // given
        val userId = tester.id!!
        val filter = FindSuppliedCouponFilterRequest(
            size = 7,
            cursor = null,
            startSuppliedAt = LocalDateTime.now().minusDays(7),
            endSuppliedAt = LocalDateTime.now(),
        )
        // when
        val result: UserCouponHistoryResponse = getFindSuppliedCouponHistory(userId.value, filter).expectSuccess()
        // then
        assertSoftly {
            result.content.size shouldBe 7
            result.content.all {
                it.suppliedAt.isAfter(filter.startSuppliedAt) && it.suppliedAt.isBefore(filter.endSuppliedAt)
            } shouldBe true
            result.hasNext shouldBe false
        }
    }


    private fun getFindSuppliedCouponHistory(userId: Long, req: FindSuppliedCouponFilterRequest): Response {
        return given()
                .header("user-id", userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
            .params(req.toParams())
            .get(ApiPath.USER_COUPON_HISTORY)
            .then()
            .extract()
            .response()
    }

    private fun FindSuppliedCouponFilterRequest.toParams(): Map<String, String?> =
        mapOf(
            this::size.name to this.size.toString(),
            this::cursor.name to this.cursor?.toString(),
            this::startSuppliedAt.name to this.startSuppliedAt.toString(),
            this::endSuppliedAt.name to this.endSuppliedAt.toString(),
        )
}
