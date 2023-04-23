package com.fcfs.coupon.core.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user not found"),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "coupon not found"),
    FC_COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "fc coupon event not found"),
}
