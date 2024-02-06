package testutils.concurrency

import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * 동시성 테스트를 편리하게 하기위한 utils 입니다
 */
object ConcurrencyTestUtils {

    /**
     * 테스트에서 사용하기위한 병렬 실행 메소드 입니다.
     * 만약 병렬성이 아닌 동시성을 테스트를 원한다면 코틀린 코루틴이나 다른 방법으로 동시성을 구현하여주세요.
     * 여러 쓰레드에서 동작되는 상황과 유사하게 하기위해 ExecutorService를 사용합니다
     *
     * @param action (index) -> T
     */
    fun <T>parallelExecute(
        callCount: Int,
        action: (Int) -> T
    ) : List<Future<T>> {
        val latch = CountDownLatch(callCount)
        val service: ExecutorService = Executors.newFixedThreadPool(callCount)
        val futures: MutableList<Future<T>> = mutableListOf()
        //when
        repeat(callCount) {
            futures.add(service.submit<T> {
                try {
                    action(it)
                } finally {
                    latch.countDown()
                }
            })
        }
        return futures
    }
}
