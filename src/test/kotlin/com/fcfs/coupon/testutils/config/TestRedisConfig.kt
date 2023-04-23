package com.fcfs.coupon.testutils.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer

@Configuration
class TestRedisConfig(
    @Value("\${spring.data.redis.port}") redisPort: Int,
) {
    private lateinit var redisServer: RedisServer

    init {
        redisServer = RedisServer(redisPort)
    }

    @PostConstruct
    fun postConstruct() {
        redisServer.start()
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
    }
}