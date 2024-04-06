package com.fcfs.coupon.app.infra.domain.coupon.repository

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCoupon
import com.fcfs.coupon.app.core.domain.coupon.query.readmodel.IssuedCouponHistory
import com.fcfs.coupon.app.core.domain.coupon.query.repository.CouponFinder
import com.fcfs.coupon.app.core.domain.coupon.query.usecase.message.IssuedCouponFilter
import com.fcfs.coupon.app.core.domain.user.command.aggregate.UserId
import com.fcfs.coupon.app.core.exception.CustomException
import com.fcfs.coupon.app.core.exception.ErrorCode
import com.fcfs.coupon.app.infra.domain.coupon.dao.CouponJpaDao
import com.fcfs.coupon.app.infra.domain.coupon.entity.CouponEntity
import com.fcfs.coupon.app.infra.domain.user.dao.SuppliedCouponJpaDao
import com.fcfs.coupon.app.infra.domain.user.entity.SuppliedCouponEntity
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/*
 * query 패키지를 따로 둬서 구분할까도 했는데 infra에서 사용하는 것이므로 그냥 여기에 두기로 했습니다.
 */
@Repository
internal class CouponFinderImpl(
    private val suppliedCouponDao: SuppliedCouponJpaDao,
    private val couponDao: CouponJpaDao
) : CouponFinder {
    // jdsl 적용하자
    @Transactional(readOnly = true)
    override fun findAllByCouponId(filter: IssuedCouponFilter): IssuedCoupon {
        val histories = suppliedCouponDao.findAll(Pageable.ofSize(filter.size + 1)) {
            select(entity(SuppliedCouponEntity::class))
                .from(
                    entity(SuppliedCouponEntity::class),
                    fetchJoin(SuppliedCouponEntity::coupon)
                ).whereAnd(
                    path(CouponEntity::couponId).eq(filter.couponId.value),
                    path(SuppliedCouponEntity::suppliedAt).between(filter.start, filter.end),
                    filter.cursor?.let { path(SuppliedCouponEntity::suppliedAt).greaterThanOrEqualTo(it) }
                ).orderBy(
                    path(SuppliedCouponEntity::suppliedAt).asc()
                )
        }.mapNotNull { it }

        val coupon = if (histories.isEmpty()) {
            couponDao.findByIdOrNull(filter.couponId.value)
        } else {
            histories.first().coupon
        } ?: throw CustomException(ErrorCode.COUPON_NOT_FOUND)

        val (content, nextCursor) = if (histories.size > filter.size) {
            Pair(histories.take(filter.size), histories.last().suppliedAt)
        } else {
            Pair(histories, null)
        }
        return IssuedCoupon(
            couponId = CouponId(filter.couponId.value),
            name = coupon.name,
            discountAmount = coupon.discountAmount,
            start = filter.start,
            end = filter.end,
            content = content.take(filter.size).map { it.toIssuedCouponHistory() },
            nextCursor = nextCursor,
            size = filter.size
        )
    }

    private fun SuppliedCouponEntity.toIssuedCouponHistory(): IssuedCouponHistory {
        return IssuedCouponHistory(
            userId = UserId(this.user.userId ?: throw IllegalStateException("userId is null")),
            isUsed = this.isUsed,
            suppliedAt = this.suppliedAt,
            usedAt = this.usedAt
        )
    }
}
