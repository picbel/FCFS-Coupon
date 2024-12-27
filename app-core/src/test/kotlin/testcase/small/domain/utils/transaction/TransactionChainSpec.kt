package testcase.small.domain.utils.transaction

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.util.transaction.TransactionChain
import com.fcfs.coupon.app.core.util.transaction.model.TransactionChainId
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import testutils.factory.CouponFactory.randomCoupon
import testutils.factory.FirstComeCouponEventFactory.randomFirstComeCouponEvent
import testutils.factory.FirstComeCouponSupplyHistoryFactory.randomFirstComeCouponSupplyHistory
import testutils.factory.UserFactory.randomUser
import testutils.fake.repository.FakeCouponRepository
import testutils.fake.repository.FakeFirstComeCouponEventRepository
import testutils.fake.repository.FakeFirstComeCouponSupplyHistoryRepository
import testutils.fake.repository.FakeUserRepository


@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
internal class TransactionChainSpec {

    private lateinit var fcRepo: FakeFirstComeCouponEventRepository
    private lateinit var fcHistoryRepo: FakeFirstComeCouponSupplyHistoryRepository
    private lateinit var userRepo: FakeUserRepository
    private lateinit var couponRepo: FakeCouponRepository

    private lateinit var fcfsEvent : FirstComeCouponEvent
    private lateinit var user : User
    private lateinit var coupon : Coupon
    private lateinit var fcfsEventHistory : FirstComeCouponSupplyHistory

    @BeforeEach
    fun setup() {
        fcfsEvent = randomFirstComeCouponEvent()
        user = randomUser(id = UserId(1))
        coupon = randomCoupon(id = CouponId(1))
        fcfsEventHistory = randomFirstComeCouponSupplyHistory(
            firstComeCouponEventId = fcfsEvent.id,
            userId = user.userId,
            couponId = coupon.id!!
        )
        fcRepo = FakeFirstComeCouponEventRepository()
        fcHistoryRepo = FakeFirstComeCouponSupplyHistoryRepository()
        userRepo = FakeUserRepository()
        couponRepo = FakeCouponRepository()
    }

    @Test
    fun `TransactionChain의 모든 트랜잭션이 성공합니다`() {
        // given :
        val txChain = TransactionChain.open()
        val tx1 = TransactionChainId.new()
        val tx2 = TransactionChainId.new()
        val tx3 = TransactionChainId.new()
        val tx4 = TransactionChainId.new()
        // when :
        val result = txChain
            .next(
                operation = { userRepo.save(user) },
                compensation = { userRepo.remove(user.id!!) },
                txId = tx1
            ).next(
                operation = { couponRepo.save(coupon) },
                compensation = { couponRepo.remove(coupon.id!!) },
                txId = tx2
            ).next(
                operation = { fcRepo.save(fcfsEvent) },
                compensation = { fcRepo.remove(fcfsEvent.id) },
                txId = tx3
            ).next(
                operation = { fcHistoryRepo.save(fcfsEventHistory) },
                compensation = { fcHistoryRepo.remove(fcfsEventHistory) },
                txId = tx4
            ).execute()

        // then :
        assertSoftly {
            result.isSuccess shouldBe true
            userRepo.findById(user.id!!) shouldBe user
            couponRepo.findById(coupon.id!!) shouldBe coupon
            fcRepo.findById(fcfsEvent.id) shouldBe fcfsEvent
            fcHistoryRepo.findAll() shouldContain fcfsEventHistory
            result.results[tx1] shouldBe user
            result.results[tx2] shouldBe coupon
            result.results[tx3] shouldBe fcfsEvent
            result.results[tx4] shouldBe fcfsEventHistory
        }

    }

    @Nested
    inner class `TransactionChain의 트랜잭션이 실패합니다` {

        @Test
        fun `마지막 트랜잭션이 실패하여 전부 롤백되고 에러를 전파합니다`() {
            // given :
            val txChain = TransactionChain.open()

            // when :
            assertThrows<UnsupportedOperationException> {
                txChain
                    .next(
                        operation = { userRepo.save(user) },
                        compensation = { userRepo.remove(user.id!!) }
                    ).next(
                        operation = { couponRepo.save(coupon) },
                        compensation = { couponRepo.remove(coupon.id!!) }
                    ).next(
                        operation = { fcRepo.save(fcfsEvent) },
                        compensation = { fcRepo.remove(fcfsEvent.id) }
                    ).next(
                        operation = { throw UnsupportedOperationException() },
                        compensation = {  }
                    ).executeWithThrow()
            }

            // then :
            assertSoftly {
                userRepo.findById(user.id!!) shouldBe null
                couponRepo.findById(coupon.id!!) shouldBe null
                fcRepo.findById(fcfsEvent.id) shouldBe null
                fcHistoryRepo.findAll() shouldNotContain fcfsEventHistory
            }
        }

        @Test
        fun `마지막 트랜잭션이 실패하여 전부 롤백됩니다`() {
            // given :
            val txChain = TransactionChain.open()

            // when :
            val result = txChain
                .next(
                    operation = { userRepo.save(user) },
                    compensation = { userRepo.remove(user.id!!) }
                ).next(
                    operation = { couponRepo.save(coupon) },
                    compensation = { couponRepo.remove(coupon.id!!) }
                ).next(
                    operation = { fcRepo.save(fcfsEvent) },
                    compensation = { fcRepo.remove(fcfsEvent.id) }
                ).next(
                    operation = { throw UnsupportedOperationException() },
                    compensation = {  }
                ).execute()

            // then :
            assertSoftly {
                result.isSuccess shouldBe false
                userRepo.findById(user.id!!) shouldBe null
                couponRepo.findById(coupon.id!!) shouldBe null
                fcRepo.findById(fcfsEvent.id) shouldBe null
                fcHistoryRepo.findAll() shouldNotContain fcfsEventHistory
            }
        }

        @Test
        fun `3번째 트랜잭션이 실패합니다 4번째 트랜잭션은 호출되지 않습니다`() {
            // given :
            val txChain = TransactionChain.open()
            val newHistory = randomFirstComeCouponSupplyHistory(
                firstComeCouponEventId = fcfsEvent.id,
                userId = randomUser(UserId(2)).userId,
                couponId = coupon.couponId
            )
            fcHistoryRepo.save(newHistory)
            // when :
            val result = txChain
                .next(
                    operation = { userRepo.save(user) },
                    compensation = { userRepo.remove(user.id!!) }
                ).next(
                    operation = { couponRepo.save(coupon) },
                    compensation = { couponRepo.remove(coupon.id!!) }
                ).next(
                    operation = { throw UnsupportedOperationException() },
                    compensation = { fcHistoryRepo.remove(newHistory) } // 실패한 트랜잭션의 보상트랜잭션은 호출되지 않습니다.
                ).next(
                    operation = { fcHistoryRepo.save(fcfsEventHistory) },
                    compensation = { fcHistoryRepo.remove(newHistory) } // 4번째 트랜잭션은 호출되지 않습니다.
                ).execute()

            // then :
            assertSoftly {
                result.isSuccess shouldBe false
                userRepo.findById(user.id!!) shouldBe null
                couponRepo.findById(coupon.id!!) shouldBe null
                fcRepo.findById(fcfsEvent.id) shouldBe null
                fcHistoryRepo.findAll() shouldContain newHistory
            }
        }
    }


}
