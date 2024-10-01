package com.fcfs.coupon.app.core.domain.firstcome.command.usecase

import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.dto.ApplyFirstComeCouponEventResult
import com.fcfs.coupon.app.core.domain.firstcome.command.dto.FirstComeCouponEventEntryResult
import com.fcfs.coupon.app.core.domain.firstcome.command.message.ApplyFirstComeCouponEventMessage
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.usecase.service.ApplyForFirstComeCouponEventDomainService
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistoriesExtendService.isTodayApplied
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
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
        val fcEvent = validateCouponEvent(message)
        val user = userRepo.getById(message.userId)
        val now = LocalDate.now()
        val history = fcHistoryRepo.findByUserIdAndSupplyDateBetween(
            userId = user.userId,
            start = now.minusDays(8),
            end = now
        )
        if (history.isTodayApplied(userId = user.userId)) {
            throw CustomException(ErrorCode.FC_COUPON_ALREADY_APPLIED)
        }
        return internalFirstComeCouponEvent(fcEvent, history, user)
    }

    private fun internalFirstComeCouponEvent(
        fcEvent: FirstComeCouponEvent,
        history: List<FirstComeCouponSupplyHistory>,
        user: User
    ): ApplyFirstComeCouponEventResult {
        return fcRepo.applyForFirstComeCouponEvent(fcEvent.id).run {
            firstComeCouponEventProcess(
                this,
                fcEvent,
                history,
                user
            )
        }

    }

    private fun firstComeCouponEventProcess(
        firstComeCouponEventEntryResult: FirstComeCouponEventEntryResult,
        fcEvent: FirstComeCouponEvent,
        history: List<FirstComeCouponSupplyHistory>,
        user: User
    ): ApplyFirstComeCouponEventResult {
        //선착순 실패시 결과 early return
        if (firstComeCouponEventEntryResult.isNotIncludedInFirstCome) {
            return ApplyFirstComeCouponEventResult(
                isIncludedInFirstCome = false,
                couponName = null,
                couponDiscountAmount = null,
                isConsecutiveCouponSupplied = false,
                order = null,
                couponId = null
            )
        }

        val coupon = couponRepo.getById(
            firstComeCouponEventEntryResult.couponId ?: throw CustomException(ErrorCode.FC_COUPON_EVENT_NOT_FOUND)
        )
        // 쿠폰 발급
        val (eventUser, supplyHistory) = supplyTodayFirstComeCoupon(fcEvent, history, user, coupon.couponId)
        /*
           * 완벽한 트랙잭션을 보장할려면 아래 두 save를 한 트랜잭션으로 묶는게 좋아보인다
           * 이건 추후에 리팩토링을 통해 개선할 예정
           *
           * saga pattern을 사용하여 이벤트를 발행하고 이벤트를 통해 처리하는 방법도 고려해볼만 하다.
           */
        fcHistoryRepo.save(supplyHistory)
        userRepo.save(eventUser)

        // 연속 쿠폰 발급
        return ApplyFirstComeCouponEventResult(
            isIncludedInFirstCome = true,
            couponName = coupon.name,
            couponDiscountAmount = coupon.discountAmount,
            isConsecutiveCouponSupplied = supplyHistory.isSupplyContinuousCoupon,
            order = firstComeCouponEventEntryResult.order,
            couponId = coupon.id
        )
    }

    private fun validateCouponEvent(message: ApplyFirstComeCouponEventMessage) =
        fcRepo.getById(message.firstComeCouponEventId).also {
            if (it.isNotValid()) {
                throw CustomException(ErrorCode.FC_COUPON_EVENT_EXPIRED)
            }
        }

}

