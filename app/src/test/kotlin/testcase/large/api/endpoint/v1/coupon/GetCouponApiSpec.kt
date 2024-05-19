package testcase.large.api.endpoint.v1.coupon

import com.fcfs.coupon.app.api.endpoint.v1.ApiPath
import com.fcfs.coupon.app.api.endpoint.v1.coupon.request.IssuedCouponFilterRequest
import com.fcfs.coupon.app.api.endpoint.v1.coupon.response.IssuedCouponResponse
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.repository.FirstComeCouponSupplyHistoryRepository
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import testcase.large.LargeTestSuite
import testutils.factory.CouponFactory.randomCoupon
import testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import testutils.factory.FirstComeCouponSupplyHistoryFactory.firstComeCouponSupplyHistoriesSetUp
import testutils.factory.UserFactory.randomUser
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.text.Charsets.UTF_8

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

    private lateinit var coupon: Coupon

    /**
     * 3명의 유저가 10일간 쿠폰을 발급받았습니다.
     */
    @BeforeEach
    fun setUp() {
        coupon = couponRepo.save(randomCoupon())
        val event = eventRepo.save(
            randomFirstComeCouponEvent(
                defaultCouponId = coupon.couponId,
                specialCouponId = couponRepo.save(randomCoupon()).couponId,
                consecutiveCouponId = couponRepo.save(randomCoupon()).couponId,
                limitCount = 10,
                specialLimitCount = 1,
                startDate = LocalDate.now().minusDays(3)
            )
        )
        repeat(3) {
            val user = userRepo.save(randomUser())
            firstComeCouponSupplyHistoriesSetUp(
                createDates = 10,
                userId = user.userId,
                couponId = coupon.couponId,
                eventId = event.id
            ).forEach {
                firstComeCouponSupplyHistoryRepo.save(it)
            }
        }

    }

    @Test
    fun `쿠폰 발급 이력을 조회합니다`() {
        // given
        val filter = IssuedCouponFilterRequest(
            cursor = null,
            size = 10,
            start = LocalDateTime.now().minusDays(10),
            end = LocalDateTime.now(),
        )
        // when
        val res : IssuedCouponResponse = mockMvc.run {
            perform(
                MockMvcRequestBuilders
                    .get(ApiPath.FIRSTCOME_EVENT_ID.replace("{id}", coupon.couponId.value.toString()))
                    .characterEncoding(UTF_8)
                    .contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
                    .params(filter.toParams())
            ).expectSuccess()
        }
        // then
        assertSoftly {
            res.content.size shouldBe 10
            res.nextCursor shouldNotBe null
        }

    }

    private fun IssuedCouponFilterRequest.toParams() = mutableMapOf<String, String>().apply {
        set("cursor", cursor.toString())
        set("size", size.toString())
        set("start", start.toString())
        set("end", end.toString())
    }
}
