package com.fcfs.coupon.app.core.domain.coupon.query.repository

import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCoupon
import com.fcfs.coupon.app.core.domain.coupon.query.usecase.message.IssuedCouponFilter

/**
 * query 모델에서 조회용 interface의 이름에 대해 고민을 많이하였는데 찾기만한다해서 finder로 명명하였습니다.
 */
interface CouponFinder {

    fun findAllByCouponId(filter: IssuedCouponFilter): IssuedCoupon
}