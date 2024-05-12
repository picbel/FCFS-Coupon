package com.fcfs.coupon.app.core.domain.coupon.query.usecase

import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCoupon
import com.fcfs.coupon.app.core.domain.coupon.query.repository.CouponFinder
import com.fcfs.coupon.app.core.domain.coupon.query.usecase.message.IssuedCouponFilter
import org.springframework.stereotype.Service

interface FindCouponUseCase {

    /**
     * 쿠폰 발급 이력 조회
     * 쿠폰을 기준으로 발급 이력을 조회한다.
     */
    fun findAllCouponHistory(filter: IssuedCouponFilter): IssuedCoupon
}

@Service
internal class FindCouponUseCaseImpl(
    private val couponFinder: CouponFinder
) : FindCouponUseCase {

    override fun findAllCouponHistory(filter: IssuedCouponFilter): IssuedCoupon {
        return couponFinder.findAllByCouponId(filter)
    }
}
