package com.fcfs.coupon.app.core.domain.user

import com.fcfs.coupon.app.core.domain.user.model.Gender
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
) {
    val userId: Long
        get() = id ?: throw IllegalStateException("unidentified user")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "User(id=$id, name='$name', email='$email', phone='$phone', birthday=$birthday, gender=$gender, address=$address)"
    }


}
