package com.fcfs.coupon.app.core.exception

enum class ErrorCode(
    val message: String
) {
    // 4xx
    USER_NOT_FOUND("사용자를 찾을수 없습니다"),
    COUPON_NOT_FOUND("쿠폰을 찾을수 없습니다"),
    FC_COUPON_EVENT_NOT_FOUND("선착순 쿠폰 이벤트를 찾을수 없습니다."),
    FC_COUPON_ALREADY_APPLIED("이미 쿠폰을 발급받으셨습니다."),
    FC_COUPON_EVENT_EXPIRED("이벤트 기간이 아닙니다."),


    // 5xx
    FC_COUPON_MATCH_ERROR("쿠폰 정보가 일치 하지 않습니다. 고객센터로 문의하여주세요")
}
