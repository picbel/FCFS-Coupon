package testutils.factory

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.time.LocalDate
import java.time.LocalDateTime

object FirstComeCouponSupplyHistoryFactory {

    fun randomFirstComeCouponSupplyHistory(
        firstComeCouponEventId: FirstComeCouponEventId ,
        userId: UserId,
        couponId: CouponId,
        continuousReset: Boolean = false,
        supplyDateTime: LocalDateTime = LocalDateTime.now()
    ): FirstComeCouponSupplyHistory {
        return FirstComeCouponSupplyHistory(
            firstComeCouponEventId,
            userId,
            couponId,
            supplyDateTime,
            continuousReset
        )
    }

    fun firstComeCouponSupplyHistoriesSetUp(
        createDates: Long,
        excludedCouponDates: List<Long> = listOf(),
        userId: UserId,
        couponId: CouponId,
    ): List<FirstComeCouponSupplyHistory> {
        return (0 until createDates).reversed().mapNotNull { i ->
            if (excludedCouponDates.contains(createDates - i)) {
                null
            } else {
                val date = LocalDate.now().minusDays(i)
                randomFirstComeCouponSupplyHistory(
                    firstComeCouponEventId = FirstComeCouponEventId.newId(),
                    userId = userId,
                    couponId = couponId,
                    continuousReset = (createDates - i) % 7 == 1L, // 8일마다 카운트를 reset합니다.
                    supplyDateTime = date.atStartOfDay()
                )
            }
        }
    }
}
