package com.fcfs.coupon.app.core.domain.firstcomeHistory.command.repository

import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.time.LocalDate


interface FirstComeCouponSupplyHistoryReadOnlyRepository {
    // userId를 supplyDate를 start와 end로 받아서 해당 기간동안의 history를 조회한다.
    fun findByUserIdAndSupplyDateBetween(
        userId: UserId,
        start: LocalDate,
        end: LocalDate = LocalDate.now()
    ): List<FirstComeCouponSupplyHistory>

}

interface FirstComeCouponSupplyHistoryRepository : FirstComeCouponSupplyHistoryReadOnlyRepository {
    fun save(firstComeCouponSupplyHistory: FirstComeCouponSupplyHistory): FirstComeCouponSupplyHistory

    fun remove(firstComeCouponSupplyHistory: FirstComeCouponSupplyHistory)
}


