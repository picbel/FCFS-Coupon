package com.fcfs.coupon.testutils.temp

import com.fcfs.coupon.core.domain.firstcome.FirstComeCouponEvent
import com.fcfs.coupon.infra.domain.firstcome.dao.FirstComeCouponEventRedisDao
import com.fcfs.coupon.infra.domain.firstcome.model.FirstComeCoupon
import java.time.LocalDate

/**
 * admin 프로젝트가 없어 테스트 데이터 셋팅을 할 수가 없어 임시로 만든 클래스입니다.
 * 추후 admin 프로젝트가 생성되면 삭제해주세요.
 */
object RedisDataSetting {

    /**
     * [com.fcfs.coupon.testcase.medium.core.domain.firstcome.repository.FirstComeCouponEventRepositorySpec]의 firstComeCouponSetUp()을 copy&paste한 함수입니다.
     * 현재 관리자용 프로젝트가 없어 redis에 초기 데이터를 셋팅 할 방법이 core-repository 단계에서 없습니다.
     * 또한 core, presentation, infra를 별도로 모듈화 하여 리펙토링할 계획으로 각 내부에서 사용할 객체에는 internal 접근제한자를 사용하고 있습니다.
     * 위와 같은 이유료 presentation계층 테스트에서 해당 데이터를 사용 할 수 없습니다.
     * 따라서 어드민 프로젝트가 만들어지기 전까진 임시로 사용합니다.
     */
    internal fun saveRedisFirstComeCouponInfo(
        event: FirstComeCouponEvent,
        redisDao: FirstComeCouponEventRedisDao
    ) {
        val fcCoupons = firstComeCouponSetUp(event)
        redisDao.saveAll(fcCoupons)
    }
    private fun firstComeCouponSetUp(
        event: FirstComeCouponEvent,
    ): List<FirstComeCoupon> {
        val fcCoupons: MutableList<FirstComeCoupon> = mutableListOf()
        repeat(event.limitCount.toInt()) {
            fcCoupons.add(
                FirstComeCoupon(
                    eventId = event.id,
                    date = LocalDate.now(),
                    order = (it + 1).toLong(),
                    couponId = event.defaultCouponId
                )
            )
        }
        while (fcCoupons.count { it.couponId == event.specialCouponId } < event.specialLimitCount) {
            val random = fcCoupons.random()
            fcCoupons.remove(random)
            fcCoupons.add(random.copy(couponId = event.specialCouponId))
        }
        return fcCoupons.sortedBy { it.order }
    }
}