package com.fcfs.coupon.app.api.endpoint.v1

object ApiPath {
    private const val V1 = "/v1"
    private const val id = "{id}"
    const val FIRSTCOME_EVENT = "$V1/firstcome/event"
    const val FIRSTCOME_EVENT_ID = "$FIRSTCOME_EVENT/$id"

    const val USER = "$V1/user"
    const val USER_COUPON_HISTORY = "$USER/coupon/history"
}
