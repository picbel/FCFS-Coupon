package testcase.large

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fcfs.coupon.app.FcfsCouponApplication
import io.restassured.response.Response
import org.hamcrest.Matchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import testutils.config.TestRedisConfig

@SpringBootTest(
    properties = ["spring.data.redis.port=9991", "spring.data.redis.host=localhost"],
    classes = [
        FcfsCouponApplication::class,
        TestRedisConfig::class
    ],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@ActiveProfiles("large")
@AutoConfigureMockMvc
abstract class LargeTestSuite {

    @Autowired
    protected lateinit var mapper: ObjectMapper


    /**
     * 성공 상태(2xx) 응답을 반환하며, 응답을 지정된 타입 [T]로 매핑합니다.
     */
    protected final inline fun <reified T> Response.expectSuccess(): T {
        this.then().statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300))) // 2xx 상태 확인
        return mapper.readValue(this.body.asString())
    }

    /**
     * 성공 상태(2xx) 응답만 검증합니다.
     */
    protected fun Response.expectSuccess2xx() {
        this.then().statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300))) // 2xx 상태 확인
    }

    /**
     * 클라이언트 에러 상태(4xx)의 응답 본문을 반환합니다.
     */
    protected fun Response.expectError4xx(): String {
        this.then().statusCode(allOf(greaterThanOrEqualTo(400), lessThan(500))) // 4xx 상태 확인
        return this.body.asString()
    }
}

