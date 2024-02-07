package com.fcfs.coupon.app.infra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories
@SpringBootApplication
class AppInfraApplication

fun main(args: Array<String>) {
	runApplication<AppInfraApplication>(*args)
}
