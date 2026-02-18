package homework

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger


class SafeCounterMutex {
    private var value = 0
    private val mutex = Mutex()
    suspend fun increment() {
        delay(1)
        mutex.withLock {
            value++
        }
    }

    fun getValue(): Int = value
    suspend fun runConcurrentIncrements(
        coroutineCount: Int = 10,
        incrementsPerCoroutine: Int = 1000
    ): Int = coroutineScope {
        val jobs = List(coroutineCount) {
            launch(Dispatchers.Default) {
                repeat(incrementsPerCoroutine) {
                    increment()
                }
            }
        }
        jobs.joinAll()
        getValue()
    }
}

class SafeCounterAtomic {
    private val value = AtomicInteger(0)
    suspend fun increment() {
        delay(1)
        value.incrementAndGet()
    }

    fun getValue(): Int = value.get()
    suspend fun runConcurrentIncrements(
        coroutineCount: Int = 10,
        incrementsPerCoroutine: Int = 1000
    ): Int = coroutineScope {
        val jobs = List(coroutineCount) {
            launch(Dispatchers.Default) {
                repeat(incrementsPerCoroutine) {
                    increment()
                }
            }
        }
        jobs.joinAll()
        getValue()
    }
}