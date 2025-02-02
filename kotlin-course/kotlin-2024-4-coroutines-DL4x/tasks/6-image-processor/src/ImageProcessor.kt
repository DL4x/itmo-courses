import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

typealias ImageGenerator = (query: String) -> ByteArray

class ImageProcessor(
    private val parallelism: Int,
    private val requests: ReceiveChannel<String>,
    private val publications: SendChannel<Pair<String, ByteArray>>,
    private val generator: ImageGenerator,
) {
    private val mutex = Mutex()
    private val cache = mutableSetOf<String>()

    private suspend fun generateAndSend(value: String) {
        mutex.withLock {
            if (!cache.add(value)) return
        }
        val imageBytes = generator(value)
        publications.send(value to imageBytes)
    }

    private suspend fun process() = coroutineScope {
        launch {
            for (request in requests) {
                generateAndSend(request)
            }
        }
    }

    suspend fun run(scope: CoroutineScope) = scope.launch {
        val coroutines = List(parallelism) {
            process()
        }
        coroutines.joinAll()
        publications.close()
    }
}
