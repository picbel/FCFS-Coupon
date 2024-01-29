package coupon.testutils.fake.repository

import com.fcfs.coupon.core.domain.firstcome.FirstComeCouponEvent
import com.fcfs.coupon.core.domain.firstcome.dto.FirstComeCouponEventEntryResult
import com.fcfs.coupon.core.domain.firstcome.repository.FirstComeCouponEventRepository
import coupon.testutils.fake.FakeDao
import java.time.LocalDate
import java.util.*


/**
 * 테스트를 위한 fake dao 입니다
 */
internal class FakeFirstComeCouponEventRepository(
    override val data: MutableMap<UUID, FirstComeCouponEvent> = mutableMapOf(),
) : FirstComeCouponEventRepository, FakeDao<FirstComeCouponEvent, UUID> {
    override fun save(firstComeCouponEvent: FirstComeCouponEvent): FirstComeCouponEvent {
        return save(firstComeCouponEvent, firstComeCouponEvent.id)
    }

    override fun applyForFirstComeCouponEvent(id: UUID, date: LocalDate): FirstComeCouponEventEntryResult {
        val event = getById(id)
        return event.history.first { it.date == date }.run {
            FirstComeCouponEventEntryResult(
                order = this.supplyHistory.count().toLong(),
                couponId = event.defaultCouponId,
                isIncludedInFirstCome = supplyHistory.count().toLong() < event.limitCount
            )
        }
    }

    override fun findById(id: UUID): FirstComeCouponEvent? {
        return data[id]
    }

    override fun getById(id: UUID): FirstComeCouponEvent {
        return findById(id) ?: throw Exception("FakeFirstComeCouponEventRepository FirstComeCouponEvent Not found")
    }
}
