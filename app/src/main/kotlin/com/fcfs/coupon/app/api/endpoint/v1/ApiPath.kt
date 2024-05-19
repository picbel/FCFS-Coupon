package com.fcfs.coupon.app.api.endpoint.v1

object ApiPath {
    private const val V1 = "/v1"
    private const val id = "{id}"
    const val FIRSTCOME_EVENT = "$V1/firstcome/event"
    const val FIRSTCOME_EVENT_ID = "$FIRSTCOME_EVENT/$id"

    const val COUPON_ISSUE_ID = "$V1/coupon/issue/$id"
}
