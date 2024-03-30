package com.fcfs.coupon.app.infra.domain.coupon.repository

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.Coupon
import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import com.fcfs.coupon.app.infra.domain.coupon.dao.CouponJpaDao
import com.fcfs.coupon.app.infra.domain.coupon.entity.CouponEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class CouponRepositoryImpl(
    val dao: CouponJpaDao
) : CouponRepository {

    @Transactional
    override fun save(coupon: Coupon): Coupon {
        return dao.save(coupon.toEntity()).toDomain()
    }

    @Transactional(readOnly = true)
    override fun findById(id: CouponId): Coupon? {
        return dao.findByIdOrNull(id.value)?.toDomain()
    }

    @Transactional(readOnly = true)
    override fun getById(id: CouponId): Coupon {
        return findById(id) ?: throw CustomException(ErrorCode.COUPON_NOT_FOUND)
    }

    private fun Coupon.toEntity() = CouponEntity(
        couponId = this.id?.value,
        name = this.name,
        discountAmount = this.discountAmount
    )

    private fun CouponEntity.toDomain() = Coupon(
        id = CouponId(this.couponId ?: throw IllegalStateException("CouponEntity.couponId is null")),
        name = this.name,
        discountAmount = this.discountAmount,
    )
}
