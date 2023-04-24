package com.fcfs.coupon.core.domain.firstcome.repository

import com.fcfs.coupon.core.domain.firstcome.FirstComeCouponEvent
import com.fcfs.coupon.core.domain.firstcome.dto.FirstComeCouponEventEntryResult
import java.time.LocalDate
import java.util.*


interface FirstComeCouponEventReadOnlyRepository {
    fun findById(id: UUID): FirstComeCouponEvent?
    fun getById(id: UUID): FirstComeCouponEvent

}
interface FirstComeCouponEventRepository : FirstComeCouponEventReadOnlyRepository{

    fun save(firstComeCouponEvent: FirstComeCouponEvent): FirstComeCouponEvent

    /**
     * 선착순 이벤트에 응모합니다.
     * @return 선착순 이벤트에 응모 결과
     */
    fun applyForFirstComeCouponEvent(id: UUID, date: LocalDate = LocalDate.now()): FirstComeCouponEventEntryResult

}
