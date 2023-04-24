package com.fcfs.coupon.presentation.handler

import com.fcfs.coupon.core.common.exception.CustomException
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.fcfs.coupon.presentation"])
class ResponseHandler {
    private final val headers = HttpHeaders()
    init {
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
    }
    @ExceptionHandler(value = [CustomException::class])
    fun handlingCustomException(ex: CustomException): ResponseEntity<ErrorResponse> {

        return ResponseEntity(
            ErrorResponse(ex.errorCode.message),
            headers,
            ex.errorCode.status
        )
    }

    @ResponseBody
    data class ErrorResponse(
        val message: String,
    )

}