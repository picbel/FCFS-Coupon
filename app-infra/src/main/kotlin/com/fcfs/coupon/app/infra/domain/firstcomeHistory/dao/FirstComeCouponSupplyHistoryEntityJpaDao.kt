package com.fcfs.coupon.app.infra.domain.firstcomeHistory.dao

import com.fcfs.coupon.app.infra.domain.firstcomeHistory.entity.FirstComeCouponEventHistoryEntity
import com.fcfs.coupon.app.infra.domain.firstcomeHistory.entity.FirstComeCouponEventHistoryId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
internal interface FirstComeCouponSupplyHistoryEntityJpaDao :
    JpaRepository<FirstComeCouponEventHistoryEntity, FirstComeCouponEventHistoryId> {
    @Query("""
    SELECT h
    FROM FirstComeCouponEventHistoryEntity h
    WHERE h.id.userId = :userId
    AND h.id.supplyDateTime BETWEEN :start AND :end
    """)
    fun findByUserIdAndSupplyDateTimeBetween(
        userId: Long,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<FirstComeCouponEventHistoryEntity>
}

