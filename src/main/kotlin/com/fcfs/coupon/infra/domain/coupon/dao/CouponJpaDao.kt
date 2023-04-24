package com.fcfs.coupon.infra.domain.coupon.dao

import com.fcfs.coupon.infra.domain.coupon.entity.CouponEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
internal interface CouponJpaDao : JpaRepository<CouponEntity, Long> {
    @Query("""
        SELECT DISTINCT c
        FROM CouponEntity c
        LEFT JOIN FETCH c.suppliedHistory sh
        LEFT JOIN FETCH sh.user u
        WHERE c.couponId = :id
    """)
    fun findByIdOrNull(id: Long) : CouponEntity?
}
