package testutils.factory

import com.fcfs.coupon.app.core.domain.user.User
import com.fcfs.coupon.app.core.domain.user.UserId
import com.fcfs.coupon.app.core.domain.user.model.Gender
import com.github.javafaker.Faker
import java.time.LocalDate
import java.time.ZoneOffset

object UserFactory {
    private val faker: Faker = Faker()
    fun randomUser(
        id: UserId? = null,
        name: String = faker.name().fullName().take(15),
        email: String = faker.internet().emailAddress(),
        phone: String = faker.phoneNumber().cellPhone(),
        birthday: LocalDate? = LocalDate.ofInstant(faker.date().birthday().toInstant(), ZoneOffset.UTC), // 랜덤값이라 타임존 상관x
        gender: Gender? = Gender.entries.random(),
        address: String? = faker.address().fullAddress()
    ): User {
        return User(
            id, name, email, phone, birthday, gender, address
        )
    }
}
