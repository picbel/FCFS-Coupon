package com.fcfs.coupon.app.core.commons

interface Sliceable<C> {
    val cursor: C?
    val size: Int
}
