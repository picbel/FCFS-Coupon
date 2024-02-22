package testcase.small.domain.firstcomeHistory

import org.junit.jupiter.api.Nested

@Suppress("NonAsciiCharacters", "ClassName")
internal class FirstComeCouponSupplyHistoriesSpec {

    // 테스트 케이스 통과 시키기 given 변형 필요
    @Nested
    inner class `이벤트 연속 응모 횟수를 조회합니다` {
//        @Test
//        fun `10일 연속 선착순 성공 유저입니다`() {
//            //given
//            val sut: FirstComeCouponEvent =
//                FirstComeCouponEventFactory.setUpFirstComeCouponEvent(
//                    createDates = 10, userId = userId,
//                    couponId = couponId
//                )
//
//            //when
//            val count = sut.countNowConsecutiveCouponDays(userId)
//
//            //then 10일간 연속 응모 하여도 7일째에 초기화되기 때문에 3입니다.
//            count shouldBe 3
//        }
//
//        @Test
//        fun `20일 연속 선착순 성공 유저입니다`() {
//            //given
//            val sut: FirstComeCouponEvent =
//                FirstComeCouponEventFactory.setUpFirstComeCouponEvent(
//                    createDates = 20, userId = userId,
//                    couponId = couponId
//                )
//
//            //when
//            val count = sut.countNowConsecutiveCouponDays(userId)
//
//            //then 20일간 연속 응모 하여도 매 7일째에 초기화되기 때문에 6입니다.
//            count shouldBe 6
//        }
//
//        @Test
//        fun `3일 연속 선착순 성공 이후 다음날 연속 응모 하지 못한 유저입니다`() {
//            //given
//            val sut: FirstComeCouponEvent = FirstComeCouponEventFactory.setUpFirstComeCouponEvent(
//                createDates = 4,
//                excludedCouponDates = listOf(4),
//                userId = userId,
//                couponId = couponId
//            )
//
//            //when
//            val count = sut.countNowConsecutiveCouponDays(userId)
//
//            //then
//            count shouldBe 0
//        }
//
//        @Test
//        fun `3일 연속 선착순 성공 이후 2일뒤 선착순 응모한 유저입니다`() {
//            //given
//            val sut: FirstComeCouponEvent = FirstComeCouponEventFactory.setUpFirstComeCouponEvent(
//                createDates = 5,
//                excludedCouponDates = listOf(4),
//                userId = userId,
//                couponId = couponId
//            )
//
//            //when
//            val count = sut.countNowConsecutiveCouponDays(userId)
//
//            //then
//            count shouldBe 1
//        }
    }
}
