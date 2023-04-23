package com.fcfs.coupon.infra.domain.firstcome.entity

import com.fcfs.coupon.infra.domain.coupon.entity.CouponEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.*

@Table(name = "first_come_coupon_event")
@Entity
internal class FirstComeCouponEventEntity(
    @Id
    @Column(name = "event_id", columnDefinition = "BINARY(16)")
    val eventId: UUID,
    @Column(name = "name", columnDefinition = "VARCHAR2(255)")
    val name: String,
    @Column(name = "description", columnDefinition = "VARCHAR2(255)")
    val description: String,
    @Column(name = "limit_count", columnDefinition = "NUMBER")
    val limitCount: Long,
    @Column(name = "special_limit_count", columnDefinition = "NUMBER")
    val specialLimitCount: Long,
    /**
     * history이다 보니 저장만 일어 날것이라 가정하였습니다.
     */
    @OneToMany(mappedBy = "fcEvent", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val history: MutableSet<FirstComeCouponEventHistoryEntity>,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_coupon_id")
    val defaultCoupon: CouponEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "special_coupon_id")
    val specialCoupon: CouponEntity,
    @Column(name = "start_date")
    val startDate: LocalDate,
    @Column(name = "end_date")
    val endDate: LocalDate,
)
