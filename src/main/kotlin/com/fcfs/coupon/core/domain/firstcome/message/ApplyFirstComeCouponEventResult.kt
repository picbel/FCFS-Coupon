package com.fcfs.coupon.core.domain.firstcome.message

data class ApplyFirstComeCouponEventResult(
    /**
     * 선착순 이벤트에 응모한 순서입니다.
     */
    val order: Long?,
    /**
     * 발급된 쿠폰의 id입니다. null 일시 응모에 실패하였습니다
     */
    val couponId: Long?,
    /**
     * 선착순 이벤트에 응모한 순서가 포함되었는지 여부입니다.
     */
    val isIncludedInFirstCome: Boolean
)
