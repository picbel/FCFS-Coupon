package com.fcfs.coupon.app.api.endpoint.v1.coupon



interface CouponController {
    /**
     * 유저가 자기가 발급받은 쿠폰 이력을 조회합니다
     */
    fun userFindIssuedCoupon(id: Long)
}
