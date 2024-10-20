package com.fcfs.coupon.app.infra.domain.user.dao

import com.fcfs.coupon.app.infra.domain.user.entity.SuppliedCouponEntity
import com.fcfs.coupon.app.infra.domain.user.entity.UserEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface UserJpaDao : JpaRepository<UserEntity, Long>, KotlinJdslJpqlExecutor

@Repository
internal interface SuppliedCouponJpaDao : JpaRepository<SuppliedCouponEntity, Long>, KotlinJdslJpqlExecutor
