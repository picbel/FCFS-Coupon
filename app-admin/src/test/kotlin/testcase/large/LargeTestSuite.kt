package testcase.large

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fcfs.coupon.app.AdminFcfsCouponApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import testutils.config.TestRedisConfig

@SpringBootTest(
    properties = ["spring.data.redis.port=9991", "spring.data.redis.host=localhost"],
    classes = [
        AdminFcfsCouponApplication::class,
        TestRedisConfig::class
    ],
)
@ActiveProfiles("large")
@AutoConfigureMockMvc
abstract class LargeTestSuite {

    @Autowired
    protected lateinit var mapper: ObjectMapper

    @Autowired
    protected lateinit var mockMvc: MockMvc

    // 분리 필요
    protected final inline fun <reified T> ResultActions.expectSuccess(): T {
        return mapper.readValue(
            this.andExpect(status().is2xxSuccessful)
                .andReturn()
                .response.contentAsString
        )
    }

    protected fun ResultActions.expectSuccess2xx() {
        return mapper.readValue(
            this.andExpect(status().is2xxSuccessful)
                .andReturn()
                .response.contentAsString
        )

    }

    protected fun ResultActions.expectError4xx(): String {
        return this.andExpect(status().is4xxClientError)
            .andReturn()
            .response.contentAsString
    }

    protected fun MockHttpServletRequestBuilder.params(params: Map<String, Any?>): MockHttpServletRequestBuilder {
        params.forEach { (key, value) ->
            if (value != null) {
                this.param(key, value.toString())
            }
        }
        return this
    }
}

