package com.fcfs.coupon.app.infra.domain.firstcome.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDate
import java.util.*

@Table(name = "deprecated_first_come_coupon_event_history")
@Entity
internal class DeprecatedFirstComeCouponEventHistoryEntity(
    @EmbeddedId
    val id: DeprecatedFirstComeCouponEventHistoryEntityId,
    @MapsId("fcEventId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", columnDefinition = "BINARY(16)", insertable = false, updatable = false)
    val fcEvent: FirstComeCouponEventEntity,
    /**
     * history이다 보니 저장만 일어 날것이라 가정하였습니다.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fcEventHistory", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val supplyHistory: MutableSet<DeprecatedFirstComeCouponSupplyHistoryEntity>
)

@Embeddable
open class DeprecatedFirstComeCouponEventHistoryEntityId(
    @Column(name = "event_id", columnDefinition = "BINARY(16)")
    val fcEventId: UUID,
    @Column(name = "event_date")
    val eventDate: LocalDate
) : Serializable