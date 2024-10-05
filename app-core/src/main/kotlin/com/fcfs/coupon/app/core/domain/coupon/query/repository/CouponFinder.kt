package com.fcfs.coupon.app.core.domain.coupon.query.repository

import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCoupon
import com.fcfs.coupon.app.core.domain.coupon.query.usecase.message.IssuedCouponFilter

/**
 * query 모델에서 조회용 interface의 이름에 대해 고민을 많이하였는데 찾기만한다해서 finder로 명명하였습니다.
 */
interface CouponFinder {

    // TODO 1 User id 기준으로 발급 이력을 조회하는 것으로 변경
    fun findAllByCouponId(filter: IssuedCouponFilter): IssuedCoupon
}