package com.fcfs.coupon.app.core.utils.transaction


/**
 * RDB부터 다른 연산까지 같이 묶기위한 트랜잭션 체인
 *
 * 하지만 현재 RDB만 지원하고 있다.
 *
 * RDB+MONGO+REDIS나 RDB+HTTP call 등 다양한 연산을 지원하고 싶으나
 * 현재는 저런 다양한 연산을 하나로 묶을 아이디어가 떠오르지않는다.
 */
interface TransactionChain {

    fun execute(operation:() -> Unit)

    fun <R> execute(operation:() -> R): R

}
