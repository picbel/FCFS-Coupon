package com.fcfs.coupon.app.core.domain.firstcome.command.repository

import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcome.command.dto.FirstComeCouponEventEntryResult
import java.time.LocalDate


/*
 * 2024-02-14
 * cqrs 패턴을 작업을 하면 애가 필요 있을까?
 */
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
