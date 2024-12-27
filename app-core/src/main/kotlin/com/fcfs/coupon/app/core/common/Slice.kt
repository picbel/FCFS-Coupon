package com.fcfs.coupon.app.core.common

/**
 * Slice
 * C : Cursor
 * T : Content Type
 */
interface Slice<C : Comparable<C>, T> {
    val nextCursor: C?

    val size: Int

    val content: List<T>

    val hasNext: Boolean
        get() = nextCursor != null
}
