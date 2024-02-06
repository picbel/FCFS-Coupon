package testcase.medium.infra.domain.repository

import com.fcfs.coupon.FcfsCouponApplication
import com.fcfs.coupon.app.infra.AppInfraApplication
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
    basePackageClasses = [AppInfraApplication::class]
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

    fun flushAndClear() {
        em.flush()
        em.clear()
    }
}
