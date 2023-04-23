package com.fcfs.coupon.infra.domain.firstcome.entity

import com.fcfs.coupon.infra.domain.coupon.entity.CouponEntity
import com.fcfs.coupon.infra.domain.user.entity.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinColumns
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "first_come_coupon_supply_history")
internal class FirstComeCouponSupplyHistoryEntity(
    @EmbeddedId
    val id: FirstComeCouponSupplyHistoryId,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        JoinColumn(
            name = "event_id",
            referencedColumnName = "event_id",
            columnDefinition = "BINARY(16)",
            insertable = false,
            updatable = false
        ),
        JoinColumn(name = "event_date", referencedColumnName = "event_date", insertable = false, updatable = false)
    )
    val fcEventHistory: FirstComeCouponEventHistoryEntity,
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity,
    @MapsId("couponId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    val coupon: CouponEntity,
    @Column(name = "supply_date_time", columnDefinition = "DATETIME(6)")
    val supplyDateTime: LocalDateTime,
    @Column(name = "continuous_reset")
    val continuousReset: Boolean,
)

@Embeddable
open class FirstComeCouponSupplyHistoryId(
    @Embedded
    val eventHistoryId: FirstComeCouponEventHistoryId,
    @Column(name = "user_id", insertable = false, updatable = false)
    val userId: Long,
    @Column(name = "coupon_id", insertable = false, updatable = false)
    val couponId: Long
) : Serializable