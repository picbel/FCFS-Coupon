package com.fcfs.coupon.app.api.endpoint.v1.firstcome

import com.fcfs.coupon.app.api.endpoint.v1.ApiPath
import com.fcfs.coupon.app.api.endpoint.v1.firstcome.response.EntryFirstComeCouponEventResponse
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.fcfs.coupon.app.core.domain.firstcome.command.message.ApplyFirstComeCouponEventMessage
import com.fcfs.coupon.app.core.domain.firstcome.command.usecase.FirstComeCouponEventUseCase
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.util.*


interface PostFirstComeCouponEventController {
    /**
     * 원래라면 로그인방식으로 토큰에서 가져오거나 해야하지만 구현의 간단함을 위해 header에서 받겠습니다.
     */
    @PostMapping(ApiPath.FIRSTCOME_EVENT_ID)
    fun applyForFirstComeCouponEvent(
        @PathVariable("id")
        id: String,
        @RequestHeader("user-id")
        userId: Long,
    ) : EntryFirstComeCouponEventResponse

}

@RestController
class PostFirstComeCouponEventControllerImpl(
    private val useCase: FirstComeCouponEventUseCase
) : PostFirstComeCouponEventController {
    override fun applyForFirstComeCouponEvent(id: String, userId: Long): EntryFirstComeCouponEventResponse {
       useCase.applyForFirstComeCouponEvent(
           ApplyFirstComeCouponEventMessage(
               userId = UserId(userId),
               firstComeCouponEventId = FirstComeCouponEventId(UUID.fromString(id))
           )
       ).let {
           return EntryFirstComeCouponEventResponse.from(it)
       }
    }

}
