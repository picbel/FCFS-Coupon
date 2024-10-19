package com.fcfs.coupon.app.core.domain.firstcome.query.repository

import com.fcfs.coupon.app.core.domain.firstcome.query.readmodel.FirstComeCouponEventReadModel
import java.time.LocalDateTime


interface FirstComeCouponEventFinder {

    fun findFirstComeCouponEventByDate(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<FirstComeCouponEventReadModel>

}
