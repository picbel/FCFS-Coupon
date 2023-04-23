package com.fcfs.coupon.infra.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfig {

    @Bean
    fun redissonClient(
        @Value("\${spring.data.redis.host}") redisHost: String,
        @Value("\${spring.data.redis.port}") redisPort: Int,
    ): RedissonClient = Redisson.create(Config().apply {
        useSingleServer().address = "redis://$redisHost:$redisPort"
        codec = StringCodec()
    })

}
