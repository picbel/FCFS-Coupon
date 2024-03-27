package com.fcfs.coupon.app.core.domain.firstcome.command.usecase

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.dto.ApplyFirstComeCouponEventResult
import com.fcfs.coupon.app.core.domain.firstcome.command.message.ApplyFirstComeCouponEventMessage
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.usecase.service.ApplyForFirstComeCouponEventDomainService
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistoriesExtendService.isConsecutiveCouponEligible
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistoriesExtendService.isTodayApplied
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.repository.FirstComeCouponSupplyHistoryRepository
import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import org.springframework.stereotype.Service
import java.time.LocalDate

interface FirstComeCouponEventUseCase {
    fun applyForFirstComeCouponEvent(message: ApplyFirstComeCouponEventMessage): ApplyFirstComeCouponEventResult
}

@Service
internal class FirstComeCouponEventUseCaseImpl(
    private val fcRepo: FirstComeCouponEventRepository,
    private val fcHistoryRepo: FirstComeCouponSupplyHistoryRepository,
    private val userRepo: UserRepository,
    private val couponRepo: CouponRepository
) : FirstComeCouponEventUseCase,
    ApplyForFirstComeCouponEventDomainService {
    override fun applyForFirstComeCouponEvent(
        message: ApplyFirstComeCouponEventMessage
    ): ApplyFirstComeCouponEventResult {
        // 리펙토링된 함수 구현
        val fcEvent = fcRepo.getById(message.firstComeCouponEventId)
        if (fcEvent.isNotValid()) {
            throw CustomException(ErrorCode.FC_COUPON_EVENT_EXPIRED)
        }
        val user = userRepo.getById(message.userId)
        val now = LocalDate.now()
        val history = fcHistoryRepo.findByUserIdAndSupplyDateBetween(
            userId = user.userId,
            start = now.minusDays(8), //
            end = now
        )
        if (history.isTodayApplied(userId = user.userId)) {
            throw CustomException(ErrorCode.FC_COUPON_ALREADY_APPLIED)
        }
        return fcRepo.applyForFirstComeCouponEvent(fcEvent.id).run {
            if (this.isIncludedInFirstCome) {
                val coupon =
                    couponRepo.getById(this.couponId ?: throw CustomException(ErrorCode.FC_COUPON_EVENT_NOT_FOUND))
                // 쿠폰 발급
                val supplyHistory = supplyTodayFirstComeCoupon(fcEvent, history, user.userId, coupon.couponId)
                fcHistoryRepo.save(supplyHistory)
//                couponRepo.save(suppliedCoupon)
                // 연속 쿠폰 발급
                ApplyFirstComeCouponEventResult(
                    isIncludedInFirstCome = true,
                    couponName = coupon.name,
                    couponDiscountAmount = coupon.discountAmount,
                    isConsecutiveCouponSupplied = if ((history + supplyHistory).isConsecutiveCouponEligible(user.userId)) {
                        couponRepo.save(supplyConsecutiveCoupon(fcEvent, user))
                        true
                    } else {
                        false
                    },
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

    @Deprecated("coupon 관리 주체 변경")
    private fun supplyConsecutiveCoupon(fcEvent: FirstComeCouponEvent, user: User): Coupon {
        return couponRepo.getById(fcEvent.consecutiveCouponId)
    }
}

