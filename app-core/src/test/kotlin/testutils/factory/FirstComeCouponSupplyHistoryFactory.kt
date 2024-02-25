package testutils.factory

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
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
}
