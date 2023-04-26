package com.fcfs.coupon.core.domain.firstcome.usecase

import com.fcfs.coupon.core.common.exception.CustomException
import com.fcfs.coupon.core.common.exception.ErrorCode
import com.fcfs.coupon.core.domain.coupon.Coupon
import com.fcfs.coupon.core.domain.coupon.repository.CouponRepository
import com.fcfs.coupon.core.domain.firstcome.FirstComeCouponEvent
import com.fcfs.coupon.core.domain.firstcome.dto.ApplyFirstComeCouponEventResult
import com.fcfs.coupon.core.domain.firstcome.message.ApplyFirstComeCouponEventMessage
import com.fcfs.coupon.core.domain.firstcome.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.core.domain.user.User
import com.fcfs.coupon.core.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

interface FirstComeCouponEventUseCase {
    fun applyForFirstComeCouponEvent(message: ApplyFirstComeCouponEventMessage): ApplyFirstComeCouponEventResult
}

@Service
internal class FirstComeCouponEventUseCaseImpl(
    private val fcRepo: FirstComeCouponEventRepository,
    private val userRepo: UserRepository,
    private val couponRepo: CouponRepository
) : FirstComeCouponEventUseCase, ApplyForFirstComeCouponEventDomainService {
    override fun applyForFirstComeCouponEvent(message: ApplyFirstComeCouponEventMessage): ApplyFirstComeCouponEventResult {
        val fcEvent = fcRepo.getById(message.firstComeCouponEventId)
        if (fcEvent.isNotValid()) {
            throw CustomException(ErrorCode.FC_COUPON_EVENT_EXPIRED)
        }

        val user = userRepo.getById(message.userId)
        if (fcEvent.isTodayApplied(user.userId)) {
            throw CustomException(ErrorCode.FC_COUPON_ALREADY_APPLIED)
        }

        return fcRepo.applyForFirstComeCouponEvent(fcEvent.id).run {
            if (this.isIncludedInFirstCome) {
                val coupon =
                    couponRepo.getById(this.couponId ?: throw CustomException(ErrorCode.FC_COUPON_EVENT_NOT_FOUND))
                // 쿠폰 발급
                val (suppliedFcEvent, suppliedCoupon) = supplyFirstComeCoupon(fcEvent, user, coupon)
                fcRepo.save(suppliedFcEvent)
                couponRepo.save(suppliedCoupon)
                // 연속 쿠폰 발급
                if (suppliedFcEvent.isConsecutiveCouponEligible(user.userId)) {
                    supplyConsecutiveCoupon(suppliedFcEvent, user).also {
                        couponRepo.save(it)
                    }
                }
                ApplyFirstComeCouponEventResult(
                    isIncludedInFirstCome = true,
                    couponName = coupon.name,
                    couponDiscountAmount = coupon.discountAmount,
                    isConsecutiveCouponSupplied = suppliedFcEvent.isConsecutiveCouponEligible(user.userId),
                    order = order,
                    couponId = coupon.id
                )
            } else {
                ApplyFirstComeCouponEventResult(
                    isIncludedInFirstCome = false,
                    couponName = null,
                    couponDiscountAmount = null,
                    isConsecutiveCouponSupplied = false,
                    order = null,
                    couponId = null
                )
            }
        }
    }

    private fun supplyConsecutiveCoupon(fcEvent: FirstComeCouponEvent, user: User): Coupon {
        return couponRepo.findById(fcEvent.consecutiveCouponId)?.supply(user.userId)
            ?: throw CustomException(ErrorCode.FC_COUPON_EVENT_NOT_FOUND)
    }

}
