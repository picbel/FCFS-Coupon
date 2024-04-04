package com.fcfs.coupon.app.infra.domain.coupon.repository

import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCoupon
import com.fcfs.coupon.app.core.domain.coupon.query.repository.CouponFinder
import com.fcfs.coupon.app.core.domain.coupon.query.usecase.message.IssuedCouponFilter
import com.fcfs.coupon.app.infra.domain.user.dao.SuppliedCouponJpaDao

/*
 * query 패키지를 따로 둬서 구분할까도 했는데 infra에서 사용하는 것이므로 그냥 여기에 두기로 했습니다.
 */
internal class CouponFinderImpl(
    private val suppliedCouponDao: SuppliedCouponJpaDao
) : CouponFinder {
    // jdsl 적용하자
    override fun findAllByCouponId(filter: IssuedCouponFilter): IssuedCoupon {
        TODO("Not yet implemented")
    }

}
