package testcase.medium.infra.domain.repository.firstcomeHistory

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.repository.FirstComeCouponSupplyHistoryRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import testcase.medium.infra.domain.repository.MediumTestSuite
import testutils.factory.CouponFactory
import testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import testutils.factory.FirstComeCouponSupplyHistoryFactory.randomFirstComeCouponSupplyHistory
import testutils.factory.UserFactory
import java.math.BigDecimal

@Suppress("NonAsciiCharacters", "className") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class FirstComeCouponSupplyHistoryRepositorySpec: MediumTestSuite()  {

    @Autowired
    private lateinit var sut: FirstComeCouponSupplyHistoryRepository

    @Autowired
    private lateinit var eventRepo: FirstComeCouponEventRepository

    @Autowired
    private lateinit var couponRepo: CouponRepository

    @Autowired
    private lateinit var userRepo: UserRepository

    private lateinit var user: User

    private lateinit var defaultCoupons: Coupon

    private lateinit var event: FirstComeCouponEvent
    @BeforeEach
    fun setUp() {
        user = userRepo.save(UserFactory.randomUser())
        defaultCoupons = couponRepo.save(CouponFactory.randomCoupon(discountAmount = BigDecimal(5_000)))
        val specialCoupon = couponRepo.save(CouponFactory.randomCoupon(discountAmount = BigDecimal(20_000)))
        val consecutiveCoupon = couponRepo.save(CouponFactory.randomCoupon(discountAmount = BigDecimal(50_000)))
        event = eventRepo.save(randomFirstComeCouponEvent(
            defaultCouponId = defaultCoupons.id!!,
            specialCouponId = specialCoupon.id!!,
            consecutiveCouponId = consecutiveCoupon.id!!,
        ))
        flushAndClear()
    }

    @Test
    fun `이벤트 이력을 저장합니다`() {
        //given
        val history = randomFirstComeCouponSupplyHistory(
            firstComeCouponEventId = event.id,
            userId = user.id!!,
            couponId = defaultCoupons.id!!
        )
        println("---------------------------")
        //when //then
        assertDoesNotThrow { // 조회 검증은 다른 테스트에서 진행합니다.
            sut.save(history)
            flushAndClear()
        }
    }

    fun `userId와 supplyDate를 start와 end로 받아서 해당 기간동안의 history를 조회합니다`() {
        //given
        //when
        //then
    }
}
