package com.fcfs.coupon.app.core.domain.user.command.repository

import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId


interface UserReadOnlyRepository {
    fun findById(id: UserId): User?

    fun getById(id: UserId): User

}

interface UserRepository : UserReadOnlyRepository {
    fun save(user: User): User
}
