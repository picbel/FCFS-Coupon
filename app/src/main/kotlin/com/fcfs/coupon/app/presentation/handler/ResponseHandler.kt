package com.fcfs.coupon.app.presentation.handler

import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
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
            errorToHttpStatus(ex.errorCode)
        )
    }

    fun errorToHttpStatus(error: ErrorCode): HttpStatus {
        return when (error) {
            ErrorCode.USER_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorCode.COUPON_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorCode.FC_COUPON_EVENT_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorCode.FC_COUPON_ALREADY_APPLIED -> HttpStatus.BAD_REQUEST
            ErrorCode.FC_COUPON_EVENT_EXPIRED -> HttpStatus.BAD_REQUEST
            ErrorCode.FC_COUPON_MATCH_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }

    @ResponseBody
    data class ErrorResponse(
        val message: String,
    )

}