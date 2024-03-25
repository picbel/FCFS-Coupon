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
    private var applyEventFlag: Boolean = true
) : FirstComeCouponEventRepository, FakeDao<FirstComeCouponEvent, FirstComeCouponEventId> {

    override fun save(firstComeCouponEvent: FirstComeCouponEvent): FirstComeCouponEvent {
        return save(firstComeCouponEvent, firstComeCouponEvent.id)
    }

    // Fake는 항상 성공합니다
    override fun applyForFirstComeCouponEvent(
        id: FirstComeCouponEventId,
        date: LocalDate
    ): FirstComeCouponEventEntryResult {
        val event = getById(id)

        return if (applyEventFlag) {
            FirstComeCouponEventEntryResult(
                order = 1,
                couponId = event.defaultCouponId,
                isIncludedInFirstCome = true
            )
        } else {
            FirstComeCouponEventEntryResult(null, null, false)
        }
    }

    /*
     * FakeFirstComeCouponEventRepository의 값을 조작하기 위한 함수인데 작성하면서도 이게 의미가 있는 행동인지에 대해선 의문이 든다.
     * 테스트를 위해서 만든 함수인데 그냥 usecase 테스트에서 mock을 사용하면 되는 것이 아닌가 싶다.
     * 현재는 리펙토링의 진도가 더 중요해서 이렇게 남겨두고 지나가지만 추후에 한번더 고민해볼 필요가 있을것 같다
     */
    internal fun setApplyForFirstComeCouponEventFlag(
        flag : Boolean
    ) {
        applyEventFlag = flag
    }

    override fun findById(id: FirstComeCouponEventId): FirstComeCouponEvent? {
        return data[id]
    }

    override fun getById(id: FirstComeCouponEventId): FirstComeCouponEvent {
        return findById(id) ?: throw Exception("FakeFirstComeCouponEventRepository FirstComeCouponEvent Not found")
    }
}
