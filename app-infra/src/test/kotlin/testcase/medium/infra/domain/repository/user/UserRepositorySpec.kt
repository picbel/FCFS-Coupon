package testcase.medium.infra.domain.repository.user

import com.fcfs.coupon.app.core.domain.user.command.repository.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import testcase.medium.infra.domain.repository.MediumTestSuite
import testutils.factory.UserFactory.randomUser


@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class UserRepositorySpec : MediumTestSuite() {

    @Autowired
    private lateinit var sut: UserRepository

    @Test
    fun `User를 저장합니다`() {
        //given
        val user = randomUser()
        //when
        val save = sut.save(user)
        //then
        val find = sut.getById(save.id!!)
        assertSoftly {
            find shouldBe save
            find.toString() shouldBe save.toString() // 안쪽 데이터에 List가 없기에 가능한 비교입니다
        }
    }

    @Test
    fun `User를 수정합니다`() {
        val user = randomUser()
        val save = sut.save(user)
        val modify = randomUser(id = save.id)
        //when
        val update = sut.save(modify)
        //then
        val find = sut.getById(save.id!!)
        assertSoftly {
            find shouldBe update
            find.toString() shouldBe update.toString() // 안쪽 데이터에 List가 없기에 가능한 비교입니다
            save.toString() shouldNotBe find.toString()
        }
    }

}
