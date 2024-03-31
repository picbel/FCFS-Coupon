package com.fcfs.coupon.app.core.utils

/**
 * Slice
 * C : Cursor
 * T : Content Type
 */
interface Slice<C,T> {
    val cursor: C
    val size : Int
    val content: List<T>
}
