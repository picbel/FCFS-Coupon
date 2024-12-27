package com.fcfs.coupon.app.core.util

import java.time.LocalDate
import java.time.LocalDateTime

object DateUtils {
    fun LocalDate.atEndOfDay(): LocalDateTime {
        return this.atTime(23, 59, 59, 999999999)
    }
}
