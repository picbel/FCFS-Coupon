package com.fcfs.coupon.core.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을수 없습니다"),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을수 없습니다"),
    FC_COUPON_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "선착순 쿠폰 이벤트를 찾을수 없습니다."),
    FC_COUPON_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, "이미 쿠폰을 발급받으셨습니다."),
    FC_COUPON_EVENT_EXPIRED(HttpStatus.BAD_REQUEST, "이벤트 기간이 아닙니다."),
    FC_COUPON_MATCH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "쿠폰 정보가 일치 하지 않습니다. 고객센터로 문의하여주세요")
}
