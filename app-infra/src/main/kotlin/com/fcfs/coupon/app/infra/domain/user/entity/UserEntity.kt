package com.fcfs.coupon.app.infra.domain.user.entity

import com.fcfs.coupon.app.core.domain.user.model.Gender
import jakarta.persistence.*
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
)
