package com.fcfs.coupon.app.infra.domain.coupon.entity

import com.fcfs.coupon.app.infra.domain.user.entity.SuppliedCouponEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
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
    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "coupon", cascade = [CascadeType.ALL])
    val suppliedHistory: MutableSet<SuppliedCouponEntity>,
)