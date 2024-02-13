package com.fcfs.coupon.app.core.domain.coupon.command.repository

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId

interface CouponReadOnlyRepository {
    fun findById(id: CouponId): Coupon?

    fun getById(id: CouponId): Coupon
}

interface CouponRepository : CouponReadOnlyRepository {

    fun save(coupon: Coupon): Coupon

}
