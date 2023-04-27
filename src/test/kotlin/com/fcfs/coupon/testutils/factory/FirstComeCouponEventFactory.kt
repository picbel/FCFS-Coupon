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
        description: String = faker.lorem().paragraph().take(200),
        limitCount: Long = faker.number().numberBetween(100, 10000).toLong(),
        specialLimitCount: Long = faker.number().numberBetween(1, limitCount.toInt()).toLong(),
        history: List<FirstComeCouponEventHistory> = mutableListOf(
            randomFirstComeCouponEventHistory(id)
        ),
        defaultCouponId: Long = faker.number().numberBetween(1, 10).toLong(),
        specialCouponId: Long = faker.number().numberBetween(11, 100).toLong(),
        consecutiveCouponId: Long = faker.number().numberBetween(101, 1000).toLong(),
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
            consecutiveCouponId,
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

    /**
     * createDates 만큼 진행된 FirstComeCouponEvent를 생성합니다.
     * userId로 모든 날짜에 발급받습니다.
     */
    fun setUpFirstComeCouponEvent(
        createDates: Long,
        userId: Long,
        couponId: Long,
        excludedCouponDates: List<Long> = listOf(),
        limitCount: Long = 100,
        specialLimitCount: Long = 10
    ): FirstComeCouponEvent {
        val id = UUID.randomUUID()
        return FirstComeCouponEvent(
            id = id,
            name = "test",
            description = "test",
            limitCount = limitCount,
            specialLimitCount = specialLimitCount,
            history = (0 until createDates).reversed().map { i ->
                val date = LocalDate.now().minusDays(i)
                FirstComeCouponEventHistory(
                    firstComeCouponEventId = id,
                    date = date,
                    supplyHistory = if (excludedCouponDates.contains(createDates - i)) listOf() else listOf(
                        FirstComeCouponSupplyHistory(
                            userId = userId,
                            couponId = couponId,
                            continuousReset = (createDates - i) % 7 == 1L, // 8일마다 카운트를 reset합니다.
                            supplyDateTime = date.atStartOfDay()
                        )
                    )
                )
            },
            defaultCouponId = couponId,
            specialCouponId = couponId + 1,
            consecutiveCouponId = couponId + 2,
            startDate = LocalDate.now().minusDays(createDates),
            endDate = LocalDate.now().plusDays(createDates),
        )
    }
}
