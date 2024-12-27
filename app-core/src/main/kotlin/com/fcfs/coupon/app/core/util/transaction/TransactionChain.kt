package com.fcfs.coupon.app.core.util.transaction

import com.fcfs.coupon.app.core.util.transaction.model.TransactionChainId
import com.fcfs.coupon.app.core.util.transaction.model.TransactionChainOutcome

/**
 * Saga pattern의 Orchestration을 표현하는 인터페이스이다.
 * 여러 트랜잭션을 묶어서 처리하기때문에 TransactionChain이라는 이름을 사용하였습니다.
 *
 * operation은 로컬 트랜잭션이 적용된 로직을 의미합니다.
 * 따라서 operation이 실패할 경우 이전 operation의 보상 로직을 실행합니다.
 */
interface TransactionChain {

    /**
     * @param operation 로컬 트랜잭션이 적용된 로직
     * @param compensation 트랜잭션 chain이 실패시 실행될 보상 로직
     * @param txId 트랜잭션 ID
     */
    fun <T> next(
        operation: () -> T,
        compensation: () -> Unit,
        txId: TransactionChainId = TransactionChainId.new()
    ): TransactionChain

    /**
     * next와 동일한 함수
     * @param operation 로컬 트랜잭션이 적용된 로직
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
    fun executeWithThrow(): TransactionChainOutcome

    companion object {
        fun open(): TransactionChain {
            return TransactionChainManager()
        }

        fun open(
            operation: () -> Unit,
            compensation: () -> Unit,
            txId: TransactionChainId = TransactionChainId.new()
        ): TransactionChain = open().next(operation, compensation, txId)
    }
}

internal class TransactionChainManager : TransactionChain {

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

    override fun executeWithThrow(): TransactionChainOutcome {
        return internalExecute(true)
    }

    private fun internalExecute(isThrows: Boolean) : TransactionChainOutcome{
        val actItr = actList.listIterator()
        val results = LinkedHashMap<TransactionChainId, Any?>()
        while (actItr.hasNext()) {
            val act = actItr.next()
            try {
                results[act.txId] = act.operation.invoke()
            }catch (e: Throwable) {  // 로컬 트랜잭션이 적용되었을꺼란 가정하에 동작하기때문에 실패한 시점의 트랜잭션의 보상은 실행하지 않는다
                if (actItr.hasPrevious()) {
                    actItr.previous() // 현재 트랜잭션은 스킵한다.
                }
                while (actItr.hasPrevious()) {
                    try {
                        val prevAct = actItr.previous()
                        prevAct.compensation.invoke()
                    } catch (_: Throwable) {
                        // 보상 로직이 실패해도 무시한다. log를 남기고 슬랙같은 업무툴에 알림을 보내야하지만 예제라서 생략
                    }
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

