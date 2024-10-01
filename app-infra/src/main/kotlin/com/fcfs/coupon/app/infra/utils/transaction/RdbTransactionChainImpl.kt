package com.fcfs.coupon.app.infra.utils.transaction

import com.fcfs.coupon.app.core.utils.transaction.TransactionChain
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * 임시 구현
 * 별도 프로젝트에서
 * RDB+MONGO+REDIS나 RDB+HTTP call 등의
 */
@Repository
class RdbTransactionChainImpl : TransactionChain {

    @Transactional
    override fun execute(operation: () -> Unit) {
        operation()
    }

    override fun <R> execute(operation: () -> R): R {
        return operation()
    }

}
