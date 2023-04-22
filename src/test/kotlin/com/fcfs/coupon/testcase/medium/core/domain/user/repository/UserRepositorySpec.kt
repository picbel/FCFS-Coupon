package com.fcfs.coupon.testcase.medium.core.domain.user.repository

import com.fcfs.coupon.core.domain.user.User
import com.fcfs.coupon.core.domain.user.model.Gender
import com.fcfs.coupon.core.domain.user.repository.UserRepository
import com.fcfs.coupon.testcase.medium.MediumTestSuite
import com.github.javafaker.Faker
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.ZoneOffset.UTC


@Suppress("NonAsciiCharacters") // 테스트 코드의 가독성을 위해 함수명과 클레스에 한글을 사용합니다.
class UserRepositorySpec : MediumTestSuite() {

    val faker: Faker = Faker()

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
        find shouldBe save
        find.toString() shouldBe save.toString()
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
        find shouldBe update
        find.toString() shouldBe update.toString()
        save.toString() shouldNotBe find.toString()
    }

    internal fun randomUser(
        id: Long? = null,
        name: String = faker.name().fullName().take(20),
        email: String = faker.internet().emailAddress(),
        phone: String = faker.phoneNumber().cellPhone(),
        birthday: LocalDate = LocalDate.ofInstant(faker.date().birthday().toInstant(), UTC), // 랜덤값이라 타임존 상관x
        gender: Gender = Gender.values().random(),
        address: String = faker.address().fullAddress()
    ): User {
        return User(
            id, name, email, phone, birthday, gender, address
        )
    }
}
