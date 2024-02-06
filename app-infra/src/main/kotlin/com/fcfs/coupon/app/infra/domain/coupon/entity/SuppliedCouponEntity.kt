package com.fcfs.coupon.app.infra.domain.coupon.entity

import com.fcfs.coupon.app.infra.domain.user.entity.UserEntity
import jakarta.persistence.*
import java.io.Serializable

@Embeddable
internal open class SuppliedCouponId(
    @Column(name = "coupon_id", insertable = false, updatable = false)
    val couponId: Long?,

    @Column(name = "user_id", insertable = false, updatable = false)
    val userId: Long?
) : Serializable

@Entity
internal class SuppliedCouponEntity(
    @EmbeddedId
    val id: SuppliedCouponId,
    @Column(name = "used", columnDefinition = "BOOLEAN")
    val used: Boolean,
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("couponId")
    val coupon: CouponEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    val user: UserEntity
)
