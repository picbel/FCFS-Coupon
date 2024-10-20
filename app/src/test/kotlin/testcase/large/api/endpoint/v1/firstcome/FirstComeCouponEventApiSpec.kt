package testcase.large.api.endpoint.v1.firstcome

import com.fasterxml.jackson.module.kotlin.readValue
import com.fcfs.coupon.app.api.endpoint.v1.ApiPath
import com.fcfs.coupon.app.api.endpoint.v1.firstcome.response.EntryFirstComeCouponEventResponse
import com.fcfs.coupon.app.api.endpoint.v1.firstcome.response.FirstComeCouponEventResponse
import com.fcfs.coupon.app.api.handler.ResponseHandler
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.repository.FirstComeCouponSupplyHistoryRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import com.fcfs.coupon.app.core.exception.ErrorCode
import com.fcfs.coupon.app.infra.domain.firstcome.dao.FirstComeCouponEventRedisDao
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import testcase.large.LargeTestSuite
import testutils.concurrency.ConcurrencyTestUtils.parallelExecute
import testutils.dataset.RedisDataSetting
import testutils.factory.CouponFactory
import testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import testutils.factory.UserFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.text.Charsets.UTF_8


@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
internal class FirstComeCouponEventApiSpec : LargeTestSuite() {

    @Autowired
    private lateinit var eventRepo: FirstComeCouponEventRepository

    @Autowired
    private lateinit var couponRepo: CouponRepository

    @Autowired
    private lateinit var userRepo: UserRepository

    @Autowired
    private lateinit var redisDao: FirstComeCouponEventRedisDao // 애도 internal 처리 해야하는데..

    @Autowired
    private lateinit var fcfsHistoryRepo : FirstComeCouponSupplyHistoryRepository

    lateinit var event: FirstComeCouponEvent
    lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = userRepo.save(UserFactory.randomUser())
        // user는 2일동안 이벤트에 참여하였습니다.
        val eventId = FirstComeCouponEventId.newId()
        event = randomFirstComeCouponEvent(
            id = eventId,
            defaultCouponId = couponRepo.save(CouponFactory.randomCoupon()).couponId,
            specialCouponId = couponRepo.save(CouponFactory.randomCoupon()).couponId,
            consecutiveCouponId = couponRepo.save(CouponFactory.randomCoupon()).couponId,
            limitCount = 10,
            specialLimitCount = 1,
            startDate = LocalDate.now().minusDays(3)
        )
        eventRepo.save(event)
        repeat(2) {
            fcfsHistoryRepo.save(
                FirstComeCouponSupplyHistory(
                    userId = user.userId,
                    firstComeCouponEventId = event.id,
                    couponId = event.defaultCouponId,
                    supplyDateTime = LocalDateTime.now().minusDays(it.toLong() + 1),
                    continuousReset = false,
                    isSupplyContinuousCoupon = false
                )
            )
        }

        // redisSetting 추후 프로젝트확장으로
        RedisDataSetting.saveRedisFirstComeCouponInfo(event, redisDao)
        // 새로운 이벤트 저장
        eventRepo.save(randomFirstComeCouponEvent(
            id = FirstComeCouponEventId.newId(),
            defaultCouponId = couponRepo.save(CouponFactory.randomCoupon()).couponId,
            specialCouponId = couponRepo.save(CouponFactory.randomCoupon()).couponId,
            consecutiveCouponId = couponRepo.save(CouponFactory.randomCoupon()).couponId,
            limitCount = 10,
            specialLimitCount = 1,
            startDate = LocalDate.now().minusDays(3),
            endDate = LocalDate.now().plusDays(1)
        ))
    }

    @Test
    fun `진행중인 이벤트를 조회합니다`() {
        // when
        val response : List<FirstComeCouponEventResponse> = findInProgressEventApiCall().expectSuccess()
        // then setup에서 진행중인 이벤트는 현재 2개만 저장하였습니다
        assertSoftly {
            response.size shouldBe 2
            response.any { it.id == event.id.value } shouldBe true
        }
    }

    @Test
    fun `11명이 동시에 이벤트에 응모합니다`() {
        // given
        val users = (1..10).map { userRepo.save(UserFactory.randomUser()) }

        // when
        val results: List<EntryFirstComeCouponEventResponse> = listOf<EntryFirstComeCouponEventResponse>(
            applyForFirstComeCouponEventApiCall(
                event.id.value,
                user.id!!.value
            ).expectSuccess()
        ) + parallelExecute(users.size) {
            applyForFirstComeCouponEventApiCall(event.id.value, users[it].id!!.value)
        }.map { it.get().expectSuccess() }

        // then
        val totalUserSize = users.size + 1
        assertSoftly {
            results.size shouldBe totalUserSize
            results.count { it.isSuccess } shouldBe 10
            results.count { it.couponId == event.defaultCouponId.value } shouldBe 9
            results.count { it.couponId == event.specialCouponId.value } shouldBe 1
            results.count { it.isConsecutiveCouponSupplied } shouldBe 1
        }
    }

    /**
     * 해당 테스트는 엄밀히 말하면 완벽하지 않다.
     * 현재 클라이언트에서 아주 짧은시간내 중복호출에 대해서는 방어코드가 존재하지 않는다.
     * 아래 테스트는 첫번째 api호출 완료 후 두번째 api를 호출하기 때문에 중복호출이 발생하지 않는다.
     *
     * 만약 짧은시간내에 api 중복 호출시에도 보장하고 싶다면 현재 생각나는 방법으로는 2가지 방법이 있다.
     *
     * 1번 redis의 setnx or 분산락을 이용한 중복호출 방지
     *  - 해당 방법은 유저의 id를 key로 이용하여 setnx or 분산락을 잡으면 유저별로 중복호출을 방지할 수 있다.
     *  - 분산락은 redisson으로 구현 할 경우 만료시간이 있어서 setnx 보단 훨씬 더 편리할것으로 생각됩니다.
     *  - 현재 구조와 가장 잘 어울리는 방법이지만 일단 lock을 다룬다는 점에서 성능적인 이슈를 고려해야한다. 또한 lock객체 관리 포인트도 추가된다.
     *
     * 2번 낙관적lock을 이용한 중복호출 방지
     *  - 유저에게 지급한 쿠폰의 도메인 모델인 FirstComeCouponSupplyHistory 낙관락을 사용하면 같은 가능 할것으로 보인다
     *  - 하지만 해당 방법은 변경해야할 점이 많고 설령 실패가 낫다해도 이미 redis에 저장된 데이터를 롤백처리를 어떻게 해야할지에 대한 문제가있다
     *  - 또한 설령 진짜로 롤백을 했다해도 그것이 진정한 의미의 선착순인지는 고민을 해봐야한다
     *
     * 따라서 만약 광클로인한 중복호출을 방지하기위해서라면 1번 방식이 더 좋다 생각됩니다.
     */
    @Test
    fun `이벤트에 중복지원 할 수 없습니다`() {
        // given
        applyForFirstComeCouponEventApiCall(event.id.value, user.userId.value).expectSuccess2xx()
        // when
        val response = applyForFirstComeCouponEventApiCall(event.id.value, user.userId.value).expectError4xx().run {
            mapper.readValue<ResponseHandler.ErrorResponse>(this)
        }
        // then
        assertSoftly {
            response.message shouldBe ErrorCode.FC_COUPON_ALREADY_APPLIED.message
        }
    }


    private fun applyForFirstComeCouponEventApiCall(eventId: UUID, userId: Long): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .post(ApiPath.FIRSTCOME_EVENT_ID.replace("{id}", eventId.toString()))
                .characterEncoding(UTF_8)
                .header("user-id", userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
        )
    }

    private fun findInProgressEventApiCall(): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders
                .get(ApiPath.FIRSTCOME_EVENT_IN_PROGRESS)
                .characterEncoding(UTF_8)
                .contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
        )
    }

}
