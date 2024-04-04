package com.fcfs.coupon.app.core.domain.coupon.query.usecase.message

import com.fcfs.coupon.app.core.commons.Sliceable
import java.time.LocalDateTime

data class IssuedCouponFilter(
    override val cursor: LocalDateTime?,
    override val size: Int,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val couponId: Long
) : Sliceable<LocalDateTime>
