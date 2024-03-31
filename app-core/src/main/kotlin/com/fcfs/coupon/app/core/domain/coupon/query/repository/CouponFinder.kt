package com.fcfs.coupon.app.core.domain.coupon.query.repository

import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCoupon

/**
 * query 모델에서 조회용 interface의 이름에 대해 고민을 많이하였는데 찾기만한다해서 finder로 명명하였습니다.
 */
interface CouponFinder {

    fun findAllByCouponId(couponId: String): IssuedCoupon
}