package com.fcfs.coupon.app.core.domain.firstcome.query.usecase

import com.fcfs.coupon.app.core.domain.firstcome.query.readmodel.FirstComeCouponEventReadModel
import com.fcfs.coupon.app.core.domain.firstcome.query.repository.FirstComeCouponEventFinder
import org.springframework.stereotype.Service
import java.time.LocalDateTime


interface QueryFirstComeCouponEventUseCase {

    fun findInProgressFirstComeCouponEvent(
        now : LocalDateTime
    ) : List<FirstComeCouponEventReadModel>

}

@Service
internal class QueryFirstComeCouponEventUseCaseImpl(
    private val fcFinder : FirstComeCouponEventFinder
) : QueryFirstComeCouponEventUseCase {

    override fun findInProgressFirstComeCouponEvent(
        now: LocalDateTime
    ): List<FirstComeCouponEventReadModel> {
        return fcFinder.findInProgressFirstComeCouponEvent(now)
    }

}
