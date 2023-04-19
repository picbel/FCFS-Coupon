package com.fcfs.coupon.core.domain.coupon

import java.math.BigDecimal

/**
 * root aggregate
 */
data class Coupon(
    val id: Long?,
    val userId: Long,
    val name: String,
    val isUsed: Boolean,
    val discountAmount: BigDecimal
    // todo : 추가 보완 필요 쿠폰을 user의 모델로 두어야할까? 독립을 시켜야할까?
)
