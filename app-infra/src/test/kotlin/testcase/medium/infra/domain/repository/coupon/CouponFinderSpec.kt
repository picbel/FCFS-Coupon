package testcase.medium.infra.domain.repository.coupon

import com.fcfs.coupon.app.core.domain.coupon.query.repository.CouponFinder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import testcase.medium.infra.domain.repository.MediumTestSuite


@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class CouponFinderSpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: CouponFinder

    @Test
    fun `Coupon 발급내역을 조회합니다`() {
        //given

        //when

        //then

    }


}