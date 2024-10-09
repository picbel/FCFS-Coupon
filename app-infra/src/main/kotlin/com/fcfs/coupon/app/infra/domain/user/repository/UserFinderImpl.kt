package com.fcfs.coupon.app.infra.domain.user.repository

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.domain.user.query.readmodel.CouponHistory
import com.fcfs.coupon.app.core.domain.user.query.readmodel.UserCouponHistory
import com.fcfs.coupon.app.core.domain.user.query.repository.UserFinder
import com.fcfs.coupon.app.core.domain.user.query.usecase.message.FindSuppliedCouponFilter
import com.fcfs.coupon.app.infra.domain.user.dao.SuppliedCouponJpaDao
import com.fcfs.coupon.app.infra.domain.user.entity.SuppliedCouponEntity
import com.fcfs.coupon.app.infra.domain.user.entity.SuppliedCouponId
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class UserFinderImpl(
    private val suppliedCouponDao: SuppliedCouponJpaDao,
) : UserFinder {

    @Transactional(readOnly = true)
    override fun findSuppliedCouponHistory(userId: UserId, filter: FindSuppliedCouponFilter): UserCouponHistory {
        val suppliedCoupons = suppliedCouponDao.findAll(Pageable.ofSize(filter.size + 1)) {
            select(
                entity(SuppliedCouponEntity::class)
            ).from(
                entity(SuppliedCouponEntity::class),
                fetchJoin(SuppliedCouponEntity::coupon)
            ).whereAnd(
                path(SuppliedCouponEntity::id)(SuppliedCouponId::userId).eq(userId.value),
                path(SuppliedCouponEntity::id)(SuppliedCouponId::suppliedAt).greaterThanOrEqualTo(filter.startSuppliedAt),
                path(SuppliedCouponEntity::id)(SuppliedCouponId::suppliedAt).lessThanOrEqualTo(filter.endSuppliedAt),
                filter.cursor?.let { path(SuppliedCouponEntity::id)(SuppliedCouponId::suppliedAt).lessThanOrEqualTo(it) }
            ).orderBy(
                path(SuppliedCouponEntity::id)(SuppliedCouponId::suppliedAt).desc()
            )
        }.mapNotNull { it }

        return UserCouponHistory(
            id = userId,
            start = filter.startSuppliedAt,
            end = filter.endSuppliedAt,
            content = suppliedCoupons.map { it.toCouponHistory() }.take(filter.size),
            nextCursor = if (suppliedCoupons.size > filter.size) suppliedCoupons.last().id.suppliedAt else null,
            size = suppliedCoupons.size
        )
    }

    private fun SuppliedCouponEntity.toCouponHistory() = CouponHistory(
        couponId = CouponId(this.id.couponId),
        isUsed = this.isUsed,
        name = this.coupon.name,
        discountAmount = this.coupon.discountAmount,
        suppliedAt = this.id.suppliedAt,
        usedAt = this.usedAt
    )

}
