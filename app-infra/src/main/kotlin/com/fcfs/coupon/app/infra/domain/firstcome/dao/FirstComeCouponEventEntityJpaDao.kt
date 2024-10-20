package com.fcfs.coupon.app.infra.domain.firstcome.dao

import com.fcfs.coupon.app.infra.domain.firstcome.entity.FirstComeCouponEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
internal interface FirstComeCouponEventEntityJpaDao : JpaRepository<FirstComeCouponEventEntity, UUID> {

    @Query(
        """
        SELECT DISTINCT e
        FROM FirstComeCouponEventEntity e
        JOIN FETCH e.defaultCoupon
        JOIN FETCH e.specialCoupon
        JOIN FETCH e.consecutiveCoupon
        WHERE e.startDate <= :now
        AND e.endDate >= :now
        """
    )
    fun findAllInProgressEvents(now: LocalDate): List<FirstComeCouponEventEntity>
}

