package com.fcfs.coupon.app.infra.domain.user.dao

import com.fcfs.coupon.infra.domain.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
internal interface UserJpaDao : JpaRepository<UserEntity, Long>
