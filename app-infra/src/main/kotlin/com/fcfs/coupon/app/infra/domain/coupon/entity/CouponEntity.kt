package com.fcfs.coupon.app.infra.domain.coupon.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Table(name = "coupon")
@Entity
internal class CouponEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val couponId: Long?,
    @Column(name = "name", columnDefinition = "VARCHAR2(50)")
    val name: String,
    @Column(name = "discount_amount", columnDefinition = "DECIMAL(10,2)")
    val discountAmount: BigDecimal,
)