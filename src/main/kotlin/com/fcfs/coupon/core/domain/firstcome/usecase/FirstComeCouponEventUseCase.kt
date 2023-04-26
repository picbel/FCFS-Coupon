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
        if (fcEvent.isNotValid()) throw CustomException(ErrorCode.FC_COUPON_EVENT_EXPIRED)

        val user = userRepo.getById(message.userId)

        return if (fcEvent.isTodayApplied(user.userId)) { // 중복을 방지합니다
            throw CustomException(ErrorCode.FC_COUPON_ALREADY_APPLIED)
        } else {
            applyForEventFlow(fcEvent, user)
        }
    }

    /**
     * 선착순 이벤트를 시도합니다.
     */
    private fun applyForEventFlow(
        fcEvent: FirstComeCouponEvent,
        user: User
    ) : ApplyFirstComeCouponEventResult{
        fcRepo.applyForFirstComeCouponEvent(fcEvent.id).run {
            if (isIncludedInFirstCome) {
                val coupon = couponRepo.getById(
                    this.couponId ?: throw CustomException(ErrorCode.FC_COUPON_EVENT_NOT_FOUND)
                )
                return supplyFirstComeCouponAndSave(fcEvent, user, coupon).run {
                    supplyConsecutiveCouponAndSave(this, user)
                    ApplyFirstComeCouponEventResult(
                        isIncludedInFirstCome = true,
                        couponName = coupon.name,
                        couponDiscountAmount = coupon.discountAmount,
                        isConsecutiveCouponSupplied = this.isConsecutiveCouponEligible(user.userId),
                        order = order,
                        couponId = coupon.id
                    )
                }
            } else {
                return ApplyFirstComeCouponEventResult(
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

    // private 메서드라 2가지를 합쳐서 하나의 메서드로 만들었습니다.
    private fun supplyFirstComeCouponAndSave(
        fcEvent: FirstComeCouponEvent,
        user: User,
        coupon: Coupon
    ) : FirstComeCouponEvent{
        supplyFirstComeCoupon(
            fcEvent = fcEvent,
            user = user,
            coupon = coupon,
        ).also {
            it.saveDomains()
            return it.first
        }
    }

    private fun supplyConsecutiveCouponAndSave(
        it: FirstComeCouponEvent,
        user: User
    ) {
        if (it.isConsecutiveCouponEligible(user.userId)) {
            couponRepo.getById(it.consecutiveCouponId)
                .also { coupon ->
                    coupon.supply(user.userId)
                }.let { coupon ->
                    couponRepo.save(coupon)
                }
        }
    }

    private fun Pair<FirstComeCouponEvent, Coupon>.saveDomains(): Pair<FirstComeCouponEvent, Coupon> {
        return Pair(fcRepo.save(first), couponRepo.save(second))
    }

}
