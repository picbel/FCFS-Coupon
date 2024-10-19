package com.fcfs.coupon.app.core.domain.coupon.query.readmodel

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import java.math.BigDecimal

data class CouponReadModel(
    val id: CouponId,
    val name: String,
    val discountAmount: BigDecimal,
)
