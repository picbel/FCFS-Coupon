package testutils.factory

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory2
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.time.LocalDate
import java.time.LocalDateTime

object FirstComeCouponSupplyHistoryFactory {

    fun randomFirstComeCouponSupplyHistory(
        firstComeCouponEventId: FirstComeCouponEventId ,
        userId: UserId,
        couponId: CouponId,
        date: LocalDate = LocalDate.now(),
        continuousReset: Boolean = false,
        supplyDateTime: LocalDateTime = LocalDateTime.now()
    ): FirstComeCouponSupplyHistory2 {
        return FirstComeCouponSupplyHistory2(
            firstComeCouponEventId,
            date,
            userId,
            couponId,
            continuousReset,
            supplyDateTime
        )
    }
}
