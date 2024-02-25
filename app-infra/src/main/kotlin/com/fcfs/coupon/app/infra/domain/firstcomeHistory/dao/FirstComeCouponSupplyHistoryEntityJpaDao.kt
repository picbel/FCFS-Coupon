package com.fcfs.coupon.app.infra.domain.firstcomeHistory.dao

import com.fcfs.coupon.app.infra.domain.firstcomeHistory.entity.FirstComeCouponEventHistoryEntity
import com.fcfs.coupon.app.infra.domain.firstcomeHistory.entity.FirstComeCouponEventHistoryId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface FirstComeCouponSupplyHistoryEntityJpaDao :
    JpaRepository<FirstComeCouponEventHistoryEntity, FirstComeCouponEventHistoryId>

