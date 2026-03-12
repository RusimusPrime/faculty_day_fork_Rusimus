import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.Executors
import kotlinx.coroutines.*
import java.io.File

object CreateThreads {
    fun run(): List<Thread> {
        val names = listOf("Thread-A", "Thread-B", "Thread-C")
        return names.map { name ->
            Thread({
                repeat(5) {
                    println(name)
                    Thread.sleep(500)
                }
            }, name).apply { start() }
        }
    }
}

object RaceCondition {
    fun run(): Int {
        var counter = AtomicInteger(0)
        val names = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

        val threads = names.map {
            Thread({
                repeat(1000) {
                    counter.incrementAndGet()
                }
            })
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }
        return counter.toInt()
    }
}

object SynchronizedCounter {
    fun run(): Int {
        var counter = 0
        val lock = Any()

        val threads = (1..10).map {
            Thread {
                repeat(1000) {
                    synchronized(lock) {
                        counter++
                    }
                }
            }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }

        return counter
    }

}

object Deadlock {
    val lock1 = Any()
    val lock2 = Any()
    fun runDeadlock() {
        val threads_1 =
            Thread {
                synchronized(lock1) {
                    println("1 need lock1")
                    Thread.sleep(100)
                    synchronized(lock2) {
                        println("1 need lock2")
                    }
                }

            }

        val threads_2 =
            Thread {
                synchronized(lock1) {
                    println("2 need lock1")
                    Thread.sleep(100)
                    synchronized(lock2) {
                        println("2 need lock2")
                    }
                }

            }

        threads_1.start()
        threads_2.start()
        threads_1.join()
        threads_2.join()

        println("runDeadlock завершён")

    }

    fun runFixed(): Boolean {
        TODO()
    }
}

object ExecutorServiceExample {
    fun run(): List<String> {
        val executor = Executors.newFixedThreadPool(4)
        val results = mutableListOf<String>()
        val lock = Any()

        repeat(20) { i ->
            executor.submit {
                val msg = "Task $i running on ${Thread.currentThread().name}"
                Thread.sleep(200)
                synchronized(lock) {
                    results.add(msg)
                }
            }
        }
        executor.shutdown()
        while (!executor.isTerminated) {
            Thread.sleep(50)
        }
        return results
    }
}

object CoroutineLaunch {
    fun run(): List<String> = runBlocking {
        val output = mutableListOf<String>()
        val jobs = List(3) { idx ->
            launch {
                repeat(5) {
                    val msg = "Coroutine $idx - iteration $it"
                    println(msg)
                    output.add(msg)
                    delay(500)
                }
            }
        }
        jobs.joinAll()
        output
    }
}

object AsyncAwait {
    fun run(): Long = runBlocking {
        val rangeSize = 1_000_000 / 4
        val deferreds = (0 until 4).map { i ->
            async {
                val start = i * rangeSize + 1
                val end = if (i == 3) 1_000_000 else (i + 1) * rangeSize
                (start..end).sumOf { it.toLong() }
            }
        }
        deferreds.sumOf { it.await() }
    }
}

object StructuredConcurrency {
    fun run(failingCoroutineIndex: Int): Int = runBlocking {
        var completedCount = 0
        try {
            coroutineScope {
                repeat(5) { i ->
                    launch {
                        if (i == failingCoroutineIndex) throw RuntimeException("Failure in coroutine $i")
                        delay(100)
                        completedCount++
                    }
                }
            }
        } catch (e: Exception) {
            // все корутины отменятся автоматически
        }
        completedCount
    }
}

object WithContextIO {
    fun run(filePaths: List<String>): Map<String, String> = runBlocking {
        val deferreds = filePaths.map { path ->
            async(Dispatchers.IO) {
                path to File(path).readText()
            }
        }
        deferreds.map { it.await() }.toMap()
    }
}

fun main() {
    val result = Deadlock.runDeadlock()

}