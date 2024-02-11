package com.fcfs.coupon.app.core.domain.user.repository

import com.fcfs.coupon.app.core.domain.user.User
import com.fcfs.coupon.app.core.domain.user.UserId


interface UserReadOnlyRepository {
    fun findById(id: UserId): User?

    fun getById(id: UserId): User

}

interface UserRepository : UserReadOnlyRepository {
    fun save(user: User): User
}
