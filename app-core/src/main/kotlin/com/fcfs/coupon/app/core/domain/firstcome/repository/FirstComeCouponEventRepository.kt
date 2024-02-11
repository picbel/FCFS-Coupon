package com.fcfs.coupon.app.core.domain.firstcome.repository

import com.fcfs.coupon.app.core.domain.firstcome.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcome.dto.FirstComeCouponEventEntryResult
import java.time.LocalDate


interface FirstComeCouponEventReadOnlyRepository {
    fun findById(id: FirstComeCouponEventId): FirstComeCouponEvent?
    fun getById(id: FirstComeCouponEventId): FirstComeCouponEvent

}

interface FirstComeCouponEventRepository : FirstComeCouponEventReadOnlyRepository {

    fun save(firstComeCouponEvent: FirstComeCouponEvent): FirstComeCouponEvent

    /**
     * 선착순 이벤트에 응모합니다.
     * @return 선착순 이벤트에 응모 결과
     */
    fun applyForFirstComeCouponEvent(
        id: FirstComeCouponEventId,
        date: LocalDate = LocalDate.now()
    ): FirstComeCouponEventEntryResult

}
