package com.fcfs.coupon.app.core.domain.firstcome.command.usecase.service

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistoriesExtendService.countNowConsecutiveCouponDays
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistoriesExtendService.isTodayApplied
import com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate.FirstComeCouponSupplyHistory
import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import java.time.LocalDateTime


/**
 * 선착순 이벤트 응모 도메인 서비스
 */
internal interface ApplyForFirstComeCouponEventDomainService {
    /*
     * 여러 도메인을 묶어서 처리해야하는 요구사항의 경우 도메인서비스를 사용합니다.
     * 해당 개념은 최범균님의 DDD Start를 참고하였습니다.
     * https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=247995570
     * 위 책의 내용중 일부 요약입니다
     * 특정 기능이 응용 서비스인지 도메인 서비스인지 감을 잡기 어려울 때는 해당 로직이 애그리거트의 상태를 변경하거나 애그리거트의 상태 값을 계산하는지 검사해 보면 된다.
     * 예를 들어, 계좌 이체 로직은 계좌 애그리거트의 상태를 변경한다. 결제 금액 로직은 주문 애그리거트의의 주문 금액을 계산한다.
     * 이 두로직은 각각 애그리거트를 변경하고 애그리거트의 값을 계산하는 도메인 로직이다.
     * 도메인 로직이면서 한 애그리거트에 넣기 적합하지 않으므로 이 두 로직은 도메인 서비스로 구현하게 된다.
     *
     * 해당 서비스를 interface로 할지 class로 할지 고민이 많았습니다.
     * interface로 하면 추후 mixin처럼 사용하여 코드 재사용성이 좋다 판단되어 interface로 구현하였습니다.
     */
    @Deprecated("recordSupplyCouponHistory로 변경 예정")
            /**
             * FirstComeCouponEvent에 쿠폰 발급 내역을 기록하고 Coupon에 발급합니다.
             */
    fun deprecatedSupplyFirstComeCoupon(
        fcEvent: FirstComeCouponEvent,
        user: User,
        coupon: Coupon,
    ): Pair<FirstComeCouponEvent, Coupon> {
        return Pair(fcEvent.recordTodaySupplyCouponHistory(user.userId, coupon.couponId), coupon.supply(user.userId))
    }

    // Coupon history로 변경 하거나 삭제
    // 현재 의문인것 굳이 완성된 domain으로 받아야 할까...? 그냥 FirstComeCouponSupplyHistory의 생성자로 해결해도 될것같은 느낌?
    fun supplyTodayFirstComeCoupon(
        fcEvent: FirstComeCouponEvent,
        history: List<FirstComeCouponSupplyHistory>,
        user: User,
        coupon: Coupon,
    ): Pair<FirstComeCouponSupplyHistory, Coupon> {
        return supplyFirstComeCoupon(
            fcEvent, history, user, coupon, LocalDateTime.now()
        )
    }

    private fun supplyFirstComeCoupon(
        fcEvent: FirstComeCouponEvent,
        history: List<FirstComeCouponSupplyHistory>,
        user: User,
        coupon: Coupon,
        now: LocalDateTime
    ): Pair<FirstComeCouponSupplyHistory, Coupon> {
        fcEvent.assertSupplyCoupon(couponId = coupon.couponId)
        if (history.isTodayApplied(user.userId)) {
            throw CustomException(ErrorCode.FC_COUPON_ALREADY_APPLIED)
        }
        return Pair(
            FirstComeCouponSupplyHistory(
                firstComeCouponEventId = fcEvent.id,
                userId = user.userId,
                couponId = coupon.couponId,
                continuousReset = checkNextContinuousReset(history, user.userId),
                supplyDateTime = now
            ),
            coupon // todo 변경 필수
        )
    }

    private fun FirstComeCouponEvent.assertSupplyCoupon(couponId: CouponId) {
        if (couponId != defaultCouponId && couponId != specialCouponId) {
            throw CustomException(ErrorCode.FC_COUPON_MATCH_ERROR)
        }
    }

    private fun checkNextContinuousReset(
        history: List<FirstComeCouponSupplyHistory>,
        userId: UserId
    ): Boolean {
        return history.countNowConsecutiveCouponDays(userId) == 7L
    }

}
