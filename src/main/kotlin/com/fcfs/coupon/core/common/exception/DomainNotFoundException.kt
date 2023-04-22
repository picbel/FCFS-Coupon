package com.fcfs.coupon.core.common.exception

class DomainNotFoundException(
    errorCode: ErrorCode
) : CustomException(errorCode)
