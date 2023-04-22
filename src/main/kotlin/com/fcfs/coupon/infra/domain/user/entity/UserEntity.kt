package com.fcfs.coupon.infra.domain.user.entity

import com.fcfs.coupon.core.domain.user.model.Gender
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Table(name = "users")
@Entity
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    @Column(name = "name", columnDefinition = "VARCHAR2(15)")
    val name: String,
    @Column(name = "email", columnDefinition = "VARCHAR2(30)")
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
