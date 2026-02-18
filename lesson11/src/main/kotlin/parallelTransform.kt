package homework

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope


suspend fun <T, R> parallelTransform(
    items: List<T>,
    transform: suspend (T) -> R
): List<R> = coroutineScope {
    val deferredResults = items.map { item ->
        async {
            transform(item)
        }
    }
    deferredResults.awaitAll()
}