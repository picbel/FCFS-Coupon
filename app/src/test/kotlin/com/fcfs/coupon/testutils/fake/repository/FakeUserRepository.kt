package coupon.testutils.fake.repository

import com.fcfs.coupon.core.domain.user.User
import com.fcfs.coupon.core.domain.user.repository.UserRepository
import coupon.testutils.factory.UserFactory.randomUser
import coupon.testutils.fake.FakeDao

class FakeUserRepository(
    override val data: MutableMap<Long, User> = mutableMapOf()
) : UserRepository, FakeDao<User, Long> {
    override fun save(user: User): User {
        return if (user.id == null) {
            val id = autoIncrement()
            save(randomUser(
                id = id,
                name = user.name,
                email = user.email,
                phone = user.phone,
                birthday = user.birthday,
                gender = user.gender,
                address = user.address
            ), id)
        } else {
            save(user, user.id!!)
        }
    }

    override fun findById(id: Long): User? {
        return data[id]
    }

    override fun getById(id: Long): User {
        return findById(id) ?: throw Exception("FakeUserRepository User Not found")
    }
}