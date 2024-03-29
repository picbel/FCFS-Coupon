package com.fcfs.coupon.app.infra.domain.firstcome.dao

import com.fcfs.coupon.app.infra.domain.firstcome.entity.FirstComeCouponEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
internal interface FirstComeCouponEventEntityJpaDao : JpaRepository<FirstComeCouponEventEntity, UUID>
