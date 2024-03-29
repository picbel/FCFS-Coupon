package com.fcfs.coupon.app.infra.domain.user.entity

import com.fcfs.coupon.app.core.domain.user.command.aggregate.model.Gender
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import java.time.LocalDate

@Table(name = "users")
@Entity
internal class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Long?,
    @Column(name = "name", columnDefinition = "VARCHAR2(15)")
    val name: String,
    @Column(name = "email", columnDefinition = "VARCHAR2(50)")
    val email: String,
    @Column(name = "phone", columnDefinition = "VARCHAR2(15)")
    val phone: String,
    @Column(name = "birthday", columnDefinition = "DATE")
    val birthday: LocalDate?,
    @Column(name = "gender", columnDefinition = "VARCHAR2(6)")
    @Enumerated(EnumType.STRING)
    val gender: Gender?,
    @Column(name = "address", columnDefinition = "VARCHAR2(255)")
    val address: String?,
    /* batch size를 이용해 n+1 문제 해결 사유는 한 유저당 지급된 쿠폰엔 한계가 있을것이라 생각되서
    *
    * 하지만 spring도 아닌 hibernate에 직접 의존하게 되어 hibernate의 변경에 취약해 진다는 단점이 생긴다.
    */
    @BatchSize(size = 50)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val suppliedCoupons: MutableSet<SuppliedCouponEntity>
)

