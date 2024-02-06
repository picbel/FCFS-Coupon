package testutils.fake.repository

import com.fcfs.coupon.app.core.domain.coupon.Coupon
import com.fcfs.coupon.app.core.domain.coupon.repository.CouponRepository
import testutils.factory.CouponFactory.randomCoupon
import coupon.testutils.fake.FakeDao

class FakeCouponRepository(
    override val data: MutableMap<Long, Coupon> = mutableMapOf()
) : CouponRepository, FakeDao<Coupon, Long> {
    override fun save(coupon: Coupon): Coupon {
        return if (coupon.id == null) {
            val id = autoIncrement()
            save(randomCoupon(
                id = id,
                name = coupon.name,
                discountAmount = coupon.discountAmount,
                suppliedHistory = coupon.suppliedHistory
            ), id)
        } else {
            save(coupon, coupon.id!!)
        }
    }

    override fun findById(id: Long): Coupon? {
        return data[id]
    }

    override fun getById(id: Long): Coupon {
        return findById(id) ?: throw Exception("FakeCouponRepository Coupon Not found")
    }
}