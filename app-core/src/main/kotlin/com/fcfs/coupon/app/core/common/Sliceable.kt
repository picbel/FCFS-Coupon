package com.fcfs.coupon.app.core.common

interface Sliceable<C> {
    val cursor: C?
    val size: Int
}
