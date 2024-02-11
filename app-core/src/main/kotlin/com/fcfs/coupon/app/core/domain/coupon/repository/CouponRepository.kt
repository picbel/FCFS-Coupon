package com.fcfs.coupon.app.core.domain.coupon.repository

import com.fcfs.coupon.app.core.domain.coupon.Coupon
import com.fcfs.coupon.app.core.domain.coupon.CouponId

interface CouponReadOnlyRepository {
    fun findById(id: CouponId): Coupon?

    fun getById(id: CouponId): Coupon
}

interface CouponRepository : CouponReadOnlyRepository {

    fun save(coupon: Coupon): Coupon

}
