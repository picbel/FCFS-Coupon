package testcase.medium.infra.domain.repository.coupon

import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCoupon
import com.fcfs.coupon.app.core.domain.coupon.query.repository.CouponFinder
import com.fcfs.coupon.app.core.domain.coupon.query.usecase.message.IssuedCouponFilter
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.repository.FirstComeCouponSupplyHistoryRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.model.SuppliedCoupon
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import testcase.medium.infra.domain.repository.MediumTestSuite
import testutils.factory.CouponFactory.randomCoupon
import testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import testutils.factory.UserFactory.randomUser
import java.time.LocalDateTime


@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class CouponFinderSpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: CouponFinder

    @Autowired
    private lateinit var couponRepo: CouponRepository

    @Autowired
    private lateinit var userRepo: UserRepository

    @Autowired
    private lateinit var eventRepo: FirstComeCouponEventRepository

    @Autowired
    private lateinit var eventHistoryRepo: FirstComeCouponSupplyHistoryRepository

    private lateinit var event: FirstComeCouponEvent

    private val now : LocalDateTime = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        val users = (1..10).map { userRepo.save(randomUser()) }
        event = randomFirstComeCouponEvent(
            defaultCouponId = couponRepo.save(randomCoupon()).couponId,
            consecutiveCouponId = couponRepo.save(randomCoupon()).couponId,
            specialCouponId = couponRepo.save(randomCoupon()).couponId,
        ).run { eventRepo.save(this) }
        users.forEach { user ->
            var newUser = user
            repeat(10) {
                eventHistoryRepo.save(
                    FirstComeCouponSupplyHistory(
                        firstComeCouponEventId = event.id,
                        userId = user.userId,
                        couponId = event.defaultCouponId,
                        supplyDateTime = now.minusDays(it.toLong()),
                        continuousReset = false,
                        isSupplyContinuousCoupon = false
                    )
                )
                newUser = user.copy(suppliedCoupons = newUser.suppliedCoupons + SuppliedCoupon(
                    couponId = event.defaultCouponId,
                    suppliedAt = now.minusDays(it.toLong()),
                    isUsed = false,
                    usedAt = null
                ))
            }
            userRepo.save(newUser)
        }
        flushAndClear()
    }

    @Test
    fun `Coupon 발급내역을 조회합니다`() {
        //given 10일간 10명의 유저가 이벤트에 응모하였습니다 총 100개의 데이터가 생성됩니다
        val filter = IssuedCouponFilter(
            cursor = null,
            size = 10,
            start = now.minusDays(5),
            end = now,
            couponId = event.defaultCouponId
        )
        //when
        val issuedCoupon = sut.findAllByCouponId(filter)
        //then
        assertSoftly {
            issuedCoupon.couponId shouldBe event.defaultCouponId
            issuedCoupon.content.size shouldBe 10
            issuedCoupon.hasNext shouldBe true
        }
    }

    @Test
    fun `Coupon 발급내역을 5번 조회합니다`() {
        //given 10일간 10명의 유저가 이벤트에 응모하였습니다 총 100개의 데이터가 생성됩니다
        val filter = IssuedCouponFilter(
            cursor = null,
            size = 10,
            start = now.minusDays(4),
            end = now,
            couponId = event.defaultCouponId
        ) // 최초 4일전부터 4,3,2,1,0일 전까지 조회합니다

        //when
        val issuedCoupons : MutableList<IssuedCoupon> = mutableListOf()
        var nextCursor = filter.cursor
        do {
            val issuedCoupon = sut.findAllByCouponId(filter.copy(cursor = nextCursor))
            issuedCoupons.add(issuedCoupon)
            nextCursor = issuedCoupon.nextCursor
        } while (issuedCoupons.last().hasNext)

        //then
        val last = issuedCoupons.last()
        assertSoftly {
            issuedCoupons.random().couponId shouldBe event.defaultCouponId
            last.content.size shouldBe 10
            issuedCoupons.first().hasNext shouldBe true
            last.hasNext shouldBe false
        }
    }

    @Test
    fun `발급 이력이 없는 Coupon을 조회합니다`() {
        //given 10일간 10명의 유저가 이벤트에 응모하였습니다 총 100개의 데이터가 생성됩니다
        val filter = IssuedCouponFilter(
            cursor = null,
            size = 10,
            start = now.minusDays(5),
            end = now,
            couponId = event.specialCouponId
        )
        //when
        val issuedCoupon = sut.findAllByCouponId(filter)
        //then
        assertSoftly {
            issuedCoupon.couponId shouldBe event.specialCouponId
            issuedCoupon.content.size shouldBe 0
            issuedCoupon.hasNext shouldBe false
        }
    }




}