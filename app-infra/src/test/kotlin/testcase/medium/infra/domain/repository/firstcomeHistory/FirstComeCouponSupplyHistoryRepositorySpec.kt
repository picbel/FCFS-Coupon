package testcase.medium.infra.domain.repository.firstcomeHistory

import com.fcfs.coupon.app.core.domain.coupon.command.repository.CouponRepository
import com.fcfs.coupon.app.core.domain.firstcome.command.repository.FirstComeCouponEventRepository
import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import testcase.medium.infra.domain.repository.MediumTestSuite

@Suppress("NonAsciiCharacters", "className") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class FirstComeCouponSupplyHistoryRepositorySpec: MediumTestSuite()  {

    @Autowired
    private lateinit var eventRepo: FirstComeCouponEventRepository

    @Autowired
    private lateinit var couponRepo: CouponRepository

    @Autowired
    private lateinit var userRepo: UserRepository


    fun `이벤트 이력을 저장합니다`() {
        //given
        //when
        //then
    }

    fun `userId와 supplyDate를 start와 end로 받아서 해당 기간동안의 history를 조회합니다`() {
        //given
        //when
        //then
    }
}
