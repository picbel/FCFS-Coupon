package com.fcfs.coupon.app.core.utils.transaction.model


/**
 * TransactionChain의 실행 결과
 */
data class TransactionChainOutcome(
    /**
     * 트랜잭션 결과
     * key : TransactionChainId
     * value : 트랜잭션 실행 결과
     *
     * chain에 입력된 순서를 보장합니다.
     */
    val results: LinkedHashMap<TransactionChainId, Any?>,

    /**
     * 트랜잭션 chain 실행 성공 여부
     */
    val isSuccess: Boolean,

    /**
     * 트랜잭션 chain 실행 실패시 발생한 예외
     */
    val failure: Throwable? = null
) {
    /**
     * Id를 지정해서 가져온다면 타입까지 이미 알고있다 가정한다.
     *
     * @throws ClassCastException 타입이 맞지 않을 경우
     */
    operator fun <T> get(txId: TransactionChainId): T {
        return results[txId] as T
    }

    /**
     * 가장 마지막에 실행된 결과를 가져온다.
     *
     * @throws ClassCastException 타입이 맞지 않을 경우
     */
    fun <T> last(): T {
        return results.values.last() as T
    }

}
