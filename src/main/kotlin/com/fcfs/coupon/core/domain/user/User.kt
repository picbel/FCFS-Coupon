package com.fcfs.coupon.core.domain.user

import com.fcfs.coupon.core.domain.user.model.Gender
import java.time.LocalDate

/**
 * root aggregate
 */
data class User(
    val id: Long?,
    val name: String,
    val email: String,
    val phone: String,
    val birthday: LocalDate?,
    val gender: Gender?,
    val address: String?,
)
