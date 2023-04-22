package com.fcfs.coupon.infra.domain.user.repository

import com.fcfs.coupon.core.common.exception.DomainNotFoundException
import com.fcfs.coupon.core.common.exception.ErrorCode
import com.fcfs.coupon.core.domain.user.User
import com.fcfs.coupon.core.domain.user.repository.UserRepository
import com.fcfs.coupon.infra.domain.user.dao.UserJpaDao
import com.fcfs.coupon.infra.domain.user.entity.UserEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class UserRepositoryImpl(
    private val dao: UserJpaDao
) : UserRepository {
    @Transactional
    override fun save(user: User): User {
        return dao.save(user.toEntity()).toUser()
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): User? {
        return dao.findByIdOrNull(id)?.toUser()
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): User {
        return findById(id) ?: throw DomainNotFoundException(ErrorCode.USER_NOT_FOUND)
    }

    private fun User.toEntity(): UserEntity {
        return UserEntity(id, name, email, phone, birthday, gender, address)
    }

    private fun UserEntity.toUser(): User {
        return User(userId, name, email, phone, birthday, gender, address)
    }
}
