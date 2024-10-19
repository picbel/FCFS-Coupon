package com.fcfs.coupon.app.infra.domain.firstcome.repository

import com.fcfs.coupon.app.core.domain.firstcome.query.readmodel.FirstComeCouponEventReadModel
import com.fcfs.coupon.app.core.domain.firstcome.query.repository.FirstComeCouponEventFinder
import com.fcfs.coupon.app.infra.domain.firstcome.dao.FirstComeCouponEventEntityJpaDao
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
internal class FirstComeCouponEventFinderImpl(
    private val fcJpaDao: FirstComeCouponEventEntityJpaDao
) : FirstComeCouponEventFinder {

    override fun findFirstComeCouponEventByDate(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<FirstComeCouponEventReadModel> {
        TODO("Not yet implemented")
    }

}
