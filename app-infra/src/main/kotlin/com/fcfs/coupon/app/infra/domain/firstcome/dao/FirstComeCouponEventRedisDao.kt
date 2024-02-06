package com.fcfs.coupon.app.infra.domain.firstcome.dao

import com.fasterxml.jackson.databind.ObjectMapper
import com.fcfs.coupon.app.infra.domain.firstcome.model.FirstComeCoupon
import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*


interface FirstComeCouponEventRedisDao {
    fun applyForFirstComeCouponEvent(id: UUID, date: LocalDate): FirstComeCoupon?

    /**
     * 지금 현재 프로젝트에서는 단순히 test용 메서드
     * test를 하기위해 데이터 셋팅을 해야하는데 redis에 값을 저장할 방법이없다
     * 추후 admin용 프로젝트가 생긴다면 해당 함수를 어드민쪽으로 옮길 예정
     */
    fun saveAll(firstComeCoupons: Collection<FirstComeCoupon>): List<FirstComeCoupon>
}

/**
 * 추후 다른 서버 (특히 다른 언어의 서버)같은 경우에 redis에 접근할 수도 있기 때문에 json형태로 저장합니다.
 */
@Repository
internal class FirstComeCouponEventRedisDaoImpl(
    val client: RedissonClient,
    val objectMapper: ObjectMapper
) : FirstComeCouponEventRedisDao {
    override fun applyForFirstComeCouponEvent(id: UUID, date: LocalDate): FirstComeCoupon? {
        val queue: RScoredSortedSet<String> = getQueue(id, date)
        return queue.pollFirst()?.let { objectMapper.readValue(it, FirstComeCoupon::class.java) }
    }

    override fun saveAll(firstComeCoupons: Collection<FirstComeCoupon>): List<FirstComeCoupon> {
        if (firstComeCoupons.isEmpty()) {
            return emptyList()
        }
        val queue: RScoredSortedSet<String> = getQueue(firstComeCoupons.first().eventId, firstComeCoupons.first().date)
        return firstComeCoupons.associate { objectMapper.writeValueAsString(it) to it.order.toDouble()}.toMutableMap()
            .run { queue.addAll(this) }
            .run { queue.readAll().map { objectMapper.readValue(it, FirstComeCoupon::class.java) } }
    }

    private fun getQueue(id: UUID, date: LocalDate): RScoredSortedSet<String> =
        client.getScoredSortedSet("$FIRST_COME_COUPON:${id}:${date}")

    companion object {
        internal const val FIRST_COME_COUPON = "firstComeCoupon"
    }
}
