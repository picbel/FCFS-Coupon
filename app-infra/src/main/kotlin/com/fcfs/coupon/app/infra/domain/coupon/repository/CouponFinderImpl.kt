package com.fcfs.coupon.app.infra.domain.coupon.repository

import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCoupon
import com.fcfs.coupon.app.core.domain.coupon.query.repository.CouponFinder

/*
 * query 패키지를 따로 둬서 구분할까도 했는데 infra에서 사용하는 것이므로 그냥 여기에 두기로 했습니다.
 */
internal class CouponFinderImpl(

) : CouponFinder {
    override fun findAllByCouponId(couponId: String): IssuedCoupon {
        TODO("Not yet implemented")
    }
}
