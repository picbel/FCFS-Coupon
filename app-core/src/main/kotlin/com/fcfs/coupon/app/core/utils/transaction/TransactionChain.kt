package com.fcfs.coupon.app.core.utils.transaction

/**
 * Saga pattern의 Orchestration을 표현하는 인터페이스이다.
 * 여러 트랜잭션을 묶어서 처리하기때문에 TransactionChain이라는 이름을 사용하였다.
 */
interface TransactionChain {



    companion object {
        fun new(): TransactionChain {
            TODO()
        }
    }
}
