package com.fcfs.coupon.app.infra.domain.firstcome.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.LocalDate
import java.util.*

@JsonSerialize
@JsonDeserialize
internal data class FirstComeCoupon(
    val eventId: UUID,
    val date: LocalDate,
    /**
     * 선착순 이벤트에 응모한 순서입니다.
     */
    val order: Long,
    val couponId: Long,
)
