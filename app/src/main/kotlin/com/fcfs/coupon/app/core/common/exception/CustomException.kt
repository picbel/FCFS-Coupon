package com.fcfs.coupon.app.core.common.exception


open class CustomException(
    val errorCode: ErrorCode,
) : RuntimeException()
