package com.fcfs.coupon.core.domain.coupon.repository

import com.fcfs.coupon.core.domain.coupon.Coupon

interface CouponReadRepository {
    fun findById(id: Long): Coupon?

    fun getById(id: Long): Coupon
}

interface CouponRepository : CouponReadRepository {

    fun save(coupon: Coupon): Coupon

}
