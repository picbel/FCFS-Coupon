package coupon.testutils.fake

interface FakeDao<T, ID> {
    val data: MutableMap<ID, T>
    fun save(entity: T, id: ID): T = entity.also {
        data[id] = it
    }

    fun findById(id: ID): T? = data[id]

    fun getById(id: ID): T = findById(id) ?: throw Exception("FakeDao Not found")

    fun findAll(): List<T> = data.values.toList()

    fun autoIncrement(): Long = (data.keys.size.toLong() + 1)
}
