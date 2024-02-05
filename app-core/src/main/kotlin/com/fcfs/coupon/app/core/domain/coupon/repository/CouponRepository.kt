package com.fcfs.coupon.app.core.domain.coupon.repository

import com.fcfs.coupon.app.core.domain.coupon.Coupon

interface CouponReadOnlyRepository {
    fun findById(id: Long): Coupon?

    fun getById(id: Long): Coupon
}

interface CouponRepository : CouponReadOnlyRepository {

    fun save(coupon: Coupon): Coupon

}
