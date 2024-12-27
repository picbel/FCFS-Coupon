package com.fcfs.coupon.app.core.util.transaction.model

import java.util.*

@JvmInline
value class TransactionChainId(val value: UUID) {
    companion object {
        fun new() = TransactionChainId(UUID.randomUUID())

        fun of(value: UUID) = TransactionChainId(value)
    }
}
