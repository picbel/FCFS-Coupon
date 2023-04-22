package com.fcfs.coupon.core.common.exception


open class CustomException(
    val errorCode: ErrorCode,
) : RuntimeException()
