package testutils.fake.repository

import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcome.command.dto.FirstComeCouponEventEntryResult
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import testutils.fake.FakeDao
import java.time.LocalDate


/**
 * 테스트를 위한 fake dao 입니다
 */
internal class FakeFirstComeCouponEventRepository(
    override val data: MutableMap<FirstComeCouponEventId, FirstComeCouponEvent> = mutableMapOf(),
) : FirstComeCouponEventRepository, FakeDao<FirstComeCouponEvent, FirstComeCouponEventId> {
    override fun save(firstComeCouponEvent: FirstComeCouponEvent): FirstComeCouponEvent {
        return save(firstComeCouponEvent, firstComeCouponEvent.id)
    }

    override fun applyForFirstComeCouponEvent(
        id: FirstComeCouponEventId,
        date: LocalDate
    ): FirstComeCouponEventEntryResult {
        val event = getById(id)

        return FirstComeCouponEventEntryResult(
            order = this.supplyHistory.count().toLong(),
            couponId = event.defaultCouponId,
            isIncludedInFirstCome = supplyHistory.count().toLong() < event.limitCount
        )
    }

    override fun findById(id: FirstComeCouponEventId): FirstComeCouponEvent? {
        return data[id]
    }

    override fun getById(id: FirstComeCouponEventId): FirstComeCouponEvent {
        return findById(id) ?: throw Exception("FakeFirstComeCouponEventRepository FirstComeCouponEvent Not found")
    }
}
