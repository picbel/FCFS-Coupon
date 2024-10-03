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
     *
     * @param operation 트랜잭션 처리할 로직
     * @param compensation 트랜잭션 실패시 실행될 보상 로직
     * @param txId 트랜잭션 ID
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
     * @param txId 트랜잭션 ID
     */
    fun <T> and(
        operation: () -> T,
        compensation: () -> Unit,
        txId: TransactionChainId = TransactionChainId.new()
    ): TransactionChain

    /**
     * 쌓인 트랜잭션을 실행합니다.
     */
    fun execute(): TransactionChainOutcome

    /**
     * 쌓인 트랜잭션을 실행하고 실패시 예외를 전파합니다.
     */
    fun executeAndThrow(): TransactionChainOutcome

    companion object {
        fun open(): TransactionChain {
            return TransactionChainImpl()
        }

        fun open(
            operation: () -> Unit,
            compensation: () -> Unit,
            txId: TransactionChainId = TransactionChainId.new()
        ): TransactionChain = open().next(operation, compensation, txId)
    }
}

internal class TransactionChainImpl : TransactionChain {

    private val actList: MutableList<TxChainAct> = mutableListOf()

    override fun <T> next(operation: () -> T, compensation: () -> Unit, txId: TransactionChainId): TransactionChain {
        actList.add(
            TxChainAct(
                txId = txId,
                operation = operation,
                compensation = compensation
            )
        )
        return this
    }

    override fun <T> and(operation: () -> T, compensation: () -> Unit, txId: TransactionChainId): TransactionChain {
        return next(operation, compensation, txId)
    }

    override fun execute(): TransactionChainOutcome {
        return internalExecute(false)
    }

    override fun executeAndThrow(): TransactionChainOutcome {
        return internalExecute(true)
    }

    private fun internalExecute(isThrows: Boolean) : TransactionChainOutcome{
        val actItr = actList.listIterator()
        val results = LinkedHashMap<TransactionChainId, Any?>()
        while (actItr.hasNext()) {
            val act = actItr.next()
            try {
                results[act.txId] = act.operation.invoke()
            }catch (e: Throwable) {
                act.compensation.invoke()
                while (actItr.hasPrevious()) {
                    val prevAct = actItr.previous()
                    prevAct.compensation.invoke()
                }
                return if (isThrows) {
                    throw e
                } else {
                    results[act.txId] = e
                    TransactionChainOutcome(results, false, e)
                }
            }
        }
        return TransactionChainOutcome(results, true)
    }

    private data class TxChainAct(
        val txId: TransactionChainId,
        val operation: () -> Any?,
        val compensation: () -> Unit
    )
}

