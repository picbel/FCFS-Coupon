package com.fcfs.coupon.app.infra.domain.firstcome.entity


import com.fcfs.coupon.app.infra.domain.coupon.entity.CouponEntity
import com.fcfs.coupon.app.infra.domain.firstcomeHistory.entity.FirstComeCouponEventHistoryEntity
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Table(name = "first_come_coupon_event")
@Entity
internal open class FirstComeCouponEventEntity(
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
    @OneToMany(mappedBy = "fcEvent", fetch = FetchType.LAZY)
    val history: MutableSet<FirstComeCouponEventHistoryEntity>,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_coupon_id")
    val defaultCoupon: CouponEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "special_coupon_id")
    val specialCoupon: CouponEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consecutive_coupon_id")
    val consecutiveCoupon: CouponEntity,
    @Column(name = "start_date")
    val startDate: LocalDate,
    @Column(name = "end_date")
    val endDate: LocalDate,
)
