package testcase.medium.infra.domain.repository

import com.fcfs.coupon.app.infra.AppInfraApplication
import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import testutils.config.TestRedisConfig

@DataJpaTest
@ComponentScan(
    basePackages = [
        "com.fcfs.coupon.app.api.appconfig",
        "com.fcfs.coupon.app.infra"
    ],
    basePackageClasses = [AppInfraApplication::class]
)
@ContextConfiguration(
    classes = [
        TestRedisConfig::class,
        KotlinJdslAutoConfiguration::class
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
