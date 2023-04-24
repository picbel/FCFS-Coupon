package com.fcfs.coupon.testcase.large

import com.fasterxml.jackson.databind.ObjectMapper
import com.fcfs.coupon.FcfsCouponApplication
import com.fcfs.coupon.testutils.config.TestRedisConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(
    properties = ["spring.data.redis.port=9991", "spring.data.redis.host=localhost"],
    classes = [
        FcfsCouponApplication::class,
        TestRedisConfig::class
    ],
)
@AutoConfigureMockMvc
abstract class LargeTestSuite {

    @Autowired
    protected lateinit var mapper: ObjectMapper

    @Autowired
    protected lateinit var mockMvc: MockMvc

}