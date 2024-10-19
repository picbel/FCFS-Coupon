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