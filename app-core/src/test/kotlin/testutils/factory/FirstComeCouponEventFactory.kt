package testutils.factory

import com.fcfs.coupon.app.core.domain.coupon.command.aggregate.CouponId
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEvent
import com.fcfs.coupon.app.core.domain.firstcome.command.aggregate.FirstComeCouponEventId
import com.github.javafaker.Faker
import java.time.LocalDate

object FirstComeCouponEventFactory {
    private val faker: Faker = Faker()

    /**
     * 오늘 날짜의 FirstComeCouponEvent를 생성합니다.
     */
    fun randomFirstComeCouponEvent(
        id: FirstComeCouponEventId = FirstComeCouponEventId.newId(),
        name: String = faker.funnyName().name().take(20),
        description: String = faker.lorem().paragraph().take(200),
        limitCount: Long = faker.number().numberBetween(100, 10000).toLong(),
        specialLimitCount: Long = faker.number().numberBetween(1, limitCount.toInt()).toLong(),
        defaultCouponId: CouponId = CouponId(faker.number().numberBetween(1, 10).toLong()),
        specialCouponId: CouponId = CouponId(faker.number().numberBetween(11, 100).toLong()),
        consecutiveCouponId: CouponId = CouponId(faker.number().numberBetween(101, 1000).toLong()),
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(faker.number().numberBetween(1, 365).toLong()),
    ): FirstComeCouponEvent {
        return FirstComeCouponEvent(
            id,
            name,
            description,
            limitCount,
            specialLimitCount,
            defaultCouponId,
            specialCouponId,
            consecutiveCouponId,
            startDate,
            endDate
        )
    }

    /**
     * createDates 만큼 진행된 FirstComeCouponEvent를 생성합니다.
     * userId로 모든 날짜에 발급받습니다.
     */
    fun setUpFirstComeCouponEvent(
        createDates: Long,
        couponId: CouponId,
        limitCount: Long = 100,
        specialLimitCount: Long = 10
    ): FirstComeCouponEvent {
        val id = FirstComeCouponEventId.newId()
        return FirstComeCouponEvent(
            id = id,
            name = "test",
            description = "test",
            limitCount = limitCount,
            specialLimitCount = specialLimitCount,
            defaultCouponId = couponId,
            specialCouponId = CouponId(couponId.value + 1),
            consecutiveCouponId = CouponId(couponId.value + 2),
            startDate = LocalDate.now().minusDays(createDates),
            endDate = LocalDate.now().plusDays(createDates),
        )
    }
}
