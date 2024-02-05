package com.fcfs.coupon.app.core.exception


open class CustomException(
    val errorCode: ErrorCode,
) : RuntimeException()
