package com.fcfs.coupon.infra.domain.coupon.repository

import com.fcfs.coupon.core.common.exception.DomainNotFoundException
import com.fcfs.coupon.core.common.exception.ErrorCode
import com.fcfs.coupon.core.domain.coupon.Coupon
import com.fcfs.coupon.core.domain.coupon.model.SuppliedCoupon
import com.fcfs.coupon.core.domain.coupon.repository.CouponRepository
import com.fcfs.coupon.infra.domain.coupon.dao.CouponJpaDao
import com.fcfs.coupon.infra.domain.coupon.entity.CouponEntity
import com.fcfs.coupon.infra.domain.coupon.entity.SuppliedCouponEntity
import com.fcfs.coupon.infra.domain.coupon.entity.SuppliedCouponId
import com.fcfs.coupon.infra.domain.user.dao.UserJpaDao
import com.fcfs.coupon.infra.domain.user.entity.UserEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class CouponRepositoryImpl(
    val dao: CouponJpaDao,
    val userDao: UserJpaDao
) : CouponRepository {

    @Transactional
    override fun save(coupon: Coupon): Coupon {
        val users = userDao.findAllById(coupon.suppliedHistory.map { it.userId })
        return dao.save(coupon.toEntity(users)).toDomain()
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): Coupon? {
        return dao.findByIdOrNull(id)?.toDomain()
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): Coupon {
        return findById(id) ?: throw DomainNotFoundException(ErrorCode.COUPON_NOT_FOUND)
    }

    private fun Coupon.toEntity(
        users: List<UserEntity>
    ) = CouponEntity(
        couponId = this.id,
        name = this.name,
        discountAmount = this.discountAmount,
        suppliedHistory = mutableSetOf()
    ).apply {
        this.suppliedHistory.addAll(this@toEntity.suppliedHistory.map { it.toEntity(this, users) })
    }

    private fun SuppliedCoupon.toEntity(coupon: CouponEntity, users: List<UserEntity>): SuppliedCouponEntity {
        val user = users.find { it.userId == this.userId } ?: throw DomainNotFoundException(ErrorCode.USER_NOT_FOUND)
        return SuppliedCouponEntity(
            id = SuppliedCouponId(
                couponId = coupon.couponId,
                userId = user.userId
            ),
            used = this.isUsed,
            coupon = coupon,
            user = user
        )
    }

    private fun CouponEntity.toDomain() = Coupon(
        id = this.couponId,
        name = this.name,
        discountAmount = this.discountAmount,
        suppliedHistory = this.suppliedHistory.map { it.toDomain() }
    )

    private fun SuppliedCouponEntity.toDomain() = SuppliedCoupon(
        userId = this.id.userId ?: throw IllegalStateException("SuppliedCouponEntity.userId is null"),
        isUsed = this.used
    )
}
