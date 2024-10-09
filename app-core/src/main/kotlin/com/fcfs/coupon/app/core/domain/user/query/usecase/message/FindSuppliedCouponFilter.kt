package com.fcfs.coupon.app.core.domain.user.query.usecase.message

import com.fcfs.coupon.app.core.commons.Sliceable
import java.time.LocalDateTime

data class FindSuppliedCouponFilter(
    override val cursor: LocalDateTime?,
    override val size: Int,
    val startSuppliedAt: LocalDateTime,
    val endSuppliedAt: LocalDateTime,
) : Sliceable<LocalDateTime>
