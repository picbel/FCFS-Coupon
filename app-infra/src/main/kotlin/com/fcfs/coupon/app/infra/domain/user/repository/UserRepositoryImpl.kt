package com.fcfs.coupon.app.infra.domain.user.repository

import com.fcfs.coupon.app.core.domain.user.command.aggregate.User
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import com.fcfs.coupon.app.infra.domain.user.dao.UserJpaDao
import com.fcfs.coupon.app.infra.domain.user.entity.UserEntity
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
    override fun findById(id: UserId): User? {
        return dao.findByIdOrNull(id.value)?.toUser()
    }

    @Transactional(readOnly = true)
    override fun getById(id: UserId): User {
        return findById(id) ?: throw CustomException(ErrorCode.USER_NOT_FOUND)
    }

    // SuppliedCouponEntity를 여기서 직접 만들지 jpa로 읽어서 만들지 고민하기
    private fun User.toEntity(): UserEntity {
        return UserEntity(id?.value, name, email, phone, birthday, gender, address, mutableSetOf())
    }

    private fun UserEntity.toUser(): User {
        return User(userId?.let { UserId(it) }, name, email, phone, birthday, gender, address,
            emptyList() // TODO: Implement this
        )
    }
}
