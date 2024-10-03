package com.fcfs.coupon.app.core.utils.transaction

import com.fcfs.coupon.app.core.utils.transaction.model.TransactionChainId
import com.fcfs.coupon.app.core.utils.transaction.model.TransactionChainOutcome

/**
 * Saga pattern의 Orchestration을 표현하는 인터페이스이다.
 * 여러 트랜잭션을 묶어서 처리하기때문에 TransactionChain이라는 이름을 사용하였다.
 */
interface TransactionChain {

    /**
     *
     * @param operation 트랜잭션 처리할 로직
     * @param compensation 트랜잭션 실패시 실행될 보상 로직
     * @param txId 트랜잭션 ID
     */
    fun next(
        operation: () -> Unit,
        compensation: () -> Unit,
        txId: TransactionChainId = TransactionChainId.new()
    ): TransactionChain

    /**
     * @param operation 트랜잭션 처리할 로직
     * @param compensation 트랜잭션 실패시 실행될 보상 로직
     */
    fun <T> next(
        operation: () -> T,
        compensation: () -> Unit,
        txId: TransactionChainId = TransactionChainId.new()
    ): TransactionChain

    /**
     * next와 동일한 함수
     * @param operation 트랜잭션 처리할 로직
     * @param compensation 트랜잭션 실패시 실행될 보상 로직
     */
    fun and(
        operation: () -> Unit,
        compensation: () -> Unit,
        txId: TransactionChainId = TransactionChainId.new()
    ): TransactionChain

    /**
     * next와 동일한 함수
     * @param operation 트랜잭션 처리할 로직
     * @param compensation 트랜잭션 실패시 실행될 보상 로직
     */
    fun <T> and(
        operation: () -> T,
        compensation: () -> Unit,
        txId: TransactionChainId = TransactionChainId.new()
    ): TransactionChain

    /**
     * 쌓인 트랜잭션을 실행합니다.
     */
    fun execute() : TransactionChainOutcome

    /**
     * 쌓인 트랜잭션을 실행하고 실패시 예외를 전파합니다.
     */
    fun executeAndThrow() : TransactionChainOutcome

    companion object {
        fun open(): TransactionChain {
            return TransactionChainImpl()
        }

        fun open(
            operation: () -> Unit,
            compensation: () -> Unit,
            txId: TransactionChainId = TransactionChainId.new()
        ): TransactionChain = TransactionChain.open().next(operation, compensation, txId)
    }
}

internal class TransactionChainImpl : TransactionChain {

    override fun next(operation: () -> Unit, compensation: () -> Unit, txId: TransactionChainId): TransactionChain {
        TODO("Not yet implemented")
    }

    override fun <T> next(operation: () -> T, compensation: () -> Unit, txId: TransactionChainId): TransactionChain {
        TODO("Not yet implemented")
    }

    override fun and(operation: () -> Unit, compensation: () -> Unit, txId: TransactionChainId): TransactionChain {
        TODO("Not yet implemented")
    }

    override fun <T> and(operation: () -> T, compensation: () -> Unit, txId: TransactionChainId): TransactionChain {
        TODO("Not yet implemented")
    }

    override fun execute(): TransactionChainOutcome {
        TODO("Not yet implemented")
    }

    override fun executeAndThrow(): TransactionChainOutcome {
        TODO("Not yet implemented")
    }

}

