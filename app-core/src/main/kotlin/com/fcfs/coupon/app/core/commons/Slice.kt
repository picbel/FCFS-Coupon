package com.fcfs.coupon.app.core.commons

/**
 * Slice
 * C : Cursor
 * T : Content Type
 */
interface Slice<C,T> {
    val nextCursor: C?

    val size : Int

    val content: List<T>
    val hasNext : Boolean
        get() = nextCursor != null
}
