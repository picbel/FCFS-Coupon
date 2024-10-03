package testutils.fake.repository

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.repository.FirstComeCouponSupplyHistoryRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import testutils.fake.FakeDao
import testutils.fake.repository.FakeFirstComeCouponSupplyHistoryRepository.FakeFirstComeCouponSupplyHistoryId
import java.time.LocalDate
import java.time.LocalDateTime

class FakeFirstComeCouponSupplyHistoryRepository(
    setUpData : List<FirstComeCouponSupplyHistory> = listOf()
) : FirstComeCouponSupplyHistoryRepository,
    FakeDao<FirstComeCouponSupplyHistory, FakeFirstComeCouponSupplyHistoryId> {

    override val data: MutableMap<FakeFirstComeCouponSupplyHistoryId, FirstComeCouponSupplyHistory> = setUpData.associateBy {
        FakeFirstComeCouponSupplyHistoryId(
            firstComeCouponEventId = it.firstComeCouponEventId,
            userId = it.userId,
            couponId = it.couponId,
            supplyDateTime = it.supplyDateTime
        )
    }.toMutableMap()

    override fun save(firstComeCouponSupplyHistory: FirstComeCouponSupplyHistory): FirstComeCouponSupplyHistory {
        val id = FakeFirstComeCouponSupplyHistoryId(
            firstComeCouponEventId = firstComeCouponSupplyHistory.firstComeCouponEventId,
            userId = firstComeCouponSupplyHistory.userId,
            couponId = firstComeCouponSupplyHistory.couponId,
            supplyDateTime = firstComeCouponSupplyHistory.supplyDateTime
        )
        data[id] = firstComeCouponSupplyHistory
        return firstComeCouponSupplyHistory
    }

    override fun remove(firstComeCouponSupplyHistory: FirstComeCouponSupplyHistory) {
        val id = FakeFirstComeCouponSupplyHistoryId(
            firstComeCouponEventId = firstComeCouponSupplyHistory.firstComeCouponEventId,
            userId = firstComeCouponSupplyHistory.userId,
            couponId = firstComeCouponSupplyHistory.couponId,
            supplyDateTime = firstComeCouponSupplyHistory.supplyDateTime
        )
        data.remove(id)
    }

    override fun findByUserIdAndSupplyDateBetween(
        userId: UserId,
        start: LocalDate,
        end: LocalDate
    ): List<FirstComeCouponSupplyHistory> {
        return data.values.filter {
            it.userId == userId && it.supplyDateTime.toLocalDate() in start..end
        }
    }

    data class FakeFirstComeCouponSupplyHistoryId(
        /**
         * firstComeCouponEvent의 id
         * 해당 객체의 식별자로 활용된다
         */
        val firstComeCouponEventId: FirstComeCouponEventId,
        val userId: UserId,
        /**
         * coupon id
         * 식별자로 활용된다
         */
        val couponId: CouponId,
        /**
         * 이벤트 발급 일자 시간 정보 식별자로 활용됩니다.
         */
        val supplyDateTime: LocalDateTime,
    )
}