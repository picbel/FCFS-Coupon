package com.fcfs.coupon.infra.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.nio.ByteBuffer
import java.util.*

@Converter
class UuidConverter : AttributeConverter<UUID, ByteArray> {
    override fun convertToDatabaseColumn(attribute: UUID?): ByteArray? = attribute?.toByteArray()

    override fun convertToEntityAttribute(dbData: ByteArray?): UUID? = dbData?.toUUID()
}

private fun UUID.toByteArray(): ByteArray {
    val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(mostSignificantBits)
    bb.putLong(leastSignificantBits)
    return bb.array()
}


private fun ByteArray.toUUID(): UUID {
    if (this.size != 16) {
        throw IllegalArgumentException("16 byte 보다 큰 데이터입니다.")
    }

    val byteBuffer: ByteBuffer = ByteBuffer.wrap(this)
    val high: Long = byteBuffer.long
    val low: Long = byteBuffer.long
    return UUID(high, low)
}
