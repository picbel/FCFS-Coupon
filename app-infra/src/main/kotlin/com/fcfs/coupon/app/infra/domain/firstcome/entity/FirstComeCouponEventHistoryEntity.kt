package com.fcfs.coupon.app.infra.domain.firstcome.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDate
import java.util.*

@Table(name = "first_come_coupon_event_history")
@Entity
internal class FirstComeCouponEventHistoryEntity(
    @EmbeddedId
    val id: FirstComeCouponEventHistoryId,
    @MapsId("fcEventId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", columnDefinition = "BINARY(16)", insertable = false, updatable = false)
    val fcEvent: FirstComeCouponEventEntity,
    /**
     * history이다 보니 저장만 일어 날것이라 가정하였습니다.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fcEventHistory", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val supplyHistory: MutableSet<FirstComeCouponSupplyHistoryEntity>
)

@Embeddable
open class FirstComeCouponEventHistoryId(
    @Column(name = "event_id", columnDefinition = "BINARY(16)")
    val fcEventId: UUID,
    @Column(name = "event_date")
    val eventDate: LocalDate
) : Serializable