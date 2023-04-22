package com.fcfs.coupon.core.domain.coupon.repository

import com.fcfs.coupon.core.domain.coupon.Coupon

interface CouponRepository {
    fun save(coupon: Coupon): Coupon

    fun findById(id: Long): Coupon?

    fun getById(id: Long): Coupon
}
