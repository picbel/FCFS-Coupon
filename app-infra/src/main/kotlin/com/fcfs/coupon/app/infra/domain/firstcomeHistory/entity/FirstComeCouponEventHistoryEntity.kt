package com.fcfs.coupon.app.infra.domain.firstcomeHistory.entity

import com.fcfs.coupon.app.infra.domain.coupon.entity.CouponEntity
import com.fcfs.coupon.app.infra.domain.firstcome.entity.FirstComeCouponEventEntity
import com.fcfs.coupon.app.infra.domain.user.entity.UserEntity
import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Table(name = "first_come_coupon_event_history")
@Entity
internal open class FirstComeCouponEventHistoryEntity(
    @EmbeddedId
    val id: FirstComeCouponEventHistoryId,
    val continuousReset: Boolean,
    @MapsId("fcEventId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", columnDefinition = "BINARY(16)")
    val fcEvent: FirstComeCouponEventEntity,
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity,
    @MapsId("couponId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    val coupon: CouponEntity,
)

@Embeddable
open class FirstComeCouponEventHistoryId(
    @Column(name = "event_id", columnDefinition = "BINARY(16)", insertable = false, updatable = false)
    val fcEventId: UUID,
    @Column(name = "user_id", insertable = false, updatable = false)
    val userId: Long,
    @Column(name = "coupon_id", insertable = false, updatable = false)
    val couponId: Long,
    @Column(name = "supply_date_time")
    val supplyDateTime: LocalDateTime,
) : Serializable