package testutils.fake.repository

import com.fcfs.coupon.app.core.utils.transaction.TransactionChain

class FakeTransactionChain : TransactionChain {
    override fun execute(operation: () -> Unit) {
        operation()
    }

    override fun <R> execute(operation: () -> R): R {
        return operation()
    }
}
