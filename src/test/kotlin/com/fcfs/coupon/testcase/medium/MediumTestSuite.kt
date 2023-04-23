package com.fcfs.coupon.testcase.medium

import com.fcfs.coupon.FcfsCouponApplication
import com.fcfs.coupon.testutils.config.TestRedisConfig
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ComponentScan(
    basePackages = [
        "com.fcfs.coupon.appconfig",
        "com.fcfs.coupon.infra"
    ],
    basePackageClasses = [FcfsCouponApplication::class]
)
@ContextConfiguration(
    classes = [
        TestRedisConfig::class
    ],
)
@ActiveProfiles("medium")
@EnableAutoConfiguration
abstract class MediumTestSuite {
    @PersistenceContext
    lateinit var em: EntityManager
}
