package testutils.fake.repository

import com.fcfs.coupon.app.core.domain.user.User
import com.fcfs.coupon.app.core.domain.user.UserId
import com.fcfs.coupon.app.core.domain.user.repository.UserRepository
import testutils.factory.UserFactory.randomUser
import testutils.fake.FakeDao

class FakeUserRepository(
    override val data: MutableMap<UserId, User> = mutableMapOf()
) : UserRepository, FakeDao<User, UserId> {
    override fun save(user: User): User {
        return if (user.id == null) {
            val id = UserId(autoIncrement())
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

    override fun findById(id: UserId): User? {
        return data[id]
    }

    override fun getById(id: UserId): User {
        return findById(id) ?: throw Exception("FakeUserRepository User Not found")
    }
}