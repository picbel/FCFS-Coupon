package com.fcfs.coupon.app.core.domain.user.repository

import com.fcfs.coupon.core.domain.user.User


interface UserReadOnlyRepository {
    fun findById(id: Long): User?

    fun getById(id: Long): User

}

interface UserRepository : com.fcfs.coupon.app.core.domain.user.repository.UserReadOnlyRepository {
    fun save(user: User): User
}
