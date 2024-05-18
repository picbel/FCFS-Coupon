package com.fcfs.coupon.app.api.common.response

interface SliceResponse<C,T> {
    val content: List<T>
    val nextCursor: C?
    val size: Int
}
