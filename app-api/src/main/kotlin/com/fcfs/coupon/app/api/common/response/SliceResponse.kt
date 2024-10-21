package com.fcfs.coupon.app.api.common.response

import com.fasterxml.jackson.annotation.JsonProperty

interface SliceResponse<C,T> {
    val content: List<T>
    val nextCursor: C?
    val size: Int

    @get:JsonProperty
    val hasNext : Boolean
        get() = nextCursor != null
}
