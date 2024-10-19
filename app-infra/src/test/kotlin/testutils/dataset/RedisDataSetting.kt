package testutils.dataset

import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.infra.domain.firstcome.dao.FirstComeCouponEventRedisDao
import com.fcfs.coupon.app.infra.domain.firstcome.model.FirstComeCoupon
import java.time.LocalDate

object RedisDataSetting {

    fun saveRedisFirstComeCouponInfo(
        event: FirstComeCouponEvent,
        redisDao: FirstComeCouponEventRedisDao
    ) {
        val fcCoupons = firstComeCouponSetUp(event)
        redisDao.saveAll(fcCoupons)
    }

    /**
     * TODO 241019
     * 이 함수 app-core에 AdminFirstComeCouponEventUseCase에 구현후 해당 객체 호출되도록 변경
     * 아직 시작전인 evnet만 수정가능하도록 구현하기
     */
    private fun firstComeCouponSetUp(
        event: FirstComeCouponEvent,
    ): List<FirstComeCoupon> {
        val fcCoupons: MutableList<FirstComeCoupon> = mutableListOf()
        repeat(event.limitCount.toInt()) {
            fcCoupons.add(
                FirstComeCoupon(
                    eventId = event.id.value,
                    date = LocalDate.now(),
                    order = (it + 1).toLong(),
                    couponId = event.defaultCouponId.value
                )
            )
        }
        while (fcCoupons.count { it.couponId == event.specialCouponId.value } < event.specialLimitCount) {
            val random = fcCoupons.random()
            fcCoupons.remove(random)
            fcCoupons.add(random.copy(couponId = event.specialCouponId.value))
        }
        return fcCoupons.sortedBy { it.order }
    }
}