package com.fcfs.coupon.app.core.domain.firstcomeHistory.command.aggregate

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import java.time.LocalDate
import java.time.LocalDateTime

/*
 * 2024-02-14
 * todo : FirstComeCouponSupplyHistory과 FirstComeCouponEventHistory를 병합한것
 */
/**
 * 선착순 쿠폰 이벤트의 이력
 */
data class FirstComeCouponSupplyHistory2(
    /**
     * firstComeCouponEvent의 id
     * 해당 객체의 식별자로 활용된다
     */
    val firstComeCouponEventId: FirstComeCouponEventId,
    /**
     * 날짜
     * 해당 객체의 식별자로 활용된다
     */
    val date: LocalDate,
    /**
     * user Id
     * 식별자로 활용된다
     */
    val userId : UserId,
    /**
     * coupon id
     * 식별자로 활용된다
     */
    val couponId : CouponId,
    /**
     * 연속 선착순 일자를 reset 여부
     * 해당 필드가 true일 경우 해당필드부터 1일로 계산합니다
     */
    val continuousReset: Boolean,
    /**
     * 쿠폰 발급 일자 시간
     */
    val supplyDateTime: LocalDateTime
)
