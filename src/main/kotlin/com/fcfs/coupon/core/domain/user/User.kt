package com.fcfs.coupon.core.domain.user

import com.fcfs.coupon.core.domain.user.model.Gender
import java.time.LocalDate

/**
 * root aggregate
 */
class User(
    val id: Long?,
    val name: String,
    val email: String,
    val phone: String,
    val birthday: LocalDate?,
    val gender: Gender?,
    val address: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
