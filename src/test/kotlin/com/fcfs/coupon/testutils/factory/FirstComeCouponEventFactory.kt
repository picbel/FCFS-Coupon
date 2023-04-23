package com.fcfs.coupon.testutils.factory

import com.fcfs.coupon.core.domain.firstcome.FirstComeCouponEvent
import com.fcfs.coupon.core.domain.firstcome.model.FirstComeCouponEventHistory
import com.fcfs.coupon.core.domain.firstcome.model.FirstComeCouponSupplyHistory
import com.github.javafaker.Faker
import java.time.LocalDate
import java.util.*

object FirstComeCouponEventFactory {
    private val faker: Faker = Faker()

    /**
     * 오늘 날짜의 FirstComeCouponEvent를 생성합니다.
     */
    fun randomFirstComeCouponEvent(
        id: UUID = UUID.randomUUID(),
        name: String = faker.funnyName().name().take(20),
        description: String = faker.lorem().paragraph(),
        limitCount: Long = faker.number().numberBetween(100, 10000).toLong(),
        specialLimitCount: Long = faker.number().numberBetween(1, limitCount.toInt()).toLong(),
        history: List<FirstComeCouponEventHistory> = mutableListOf(
            randomFirstComeCouponEventHistory(id)
        ),
        defaultCouponId: Long = faker.number().numberBetween(1, 10).toLong(),
        specialCouponId: Long = faker.number().numberBetween(11, 100).toLong(),
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(faker.number().numberBetween(1, 365).toLong()),
    ): FirstComeCouponEvent {
        return FirstComeCouponEvent(
            id,
            name,
            description,
            limitCount,
            specialLimitCount,
            history,
            defaultCouponId,
            specialCouponId,
            startDate,
            endDate
        )
    }

    fun randomFirstComeCouponEventHistory(
        id: UUID,
        date: LocalDate = LocalDate.now(),
        supplyHistory: List<FirstComeCouponSupplyHistory> = listOf()
    ): FirstComeCouponEventHistory {
        return FirstComeCouponEventHistory(
            id, date, supplyHistory
        )
    }
}
