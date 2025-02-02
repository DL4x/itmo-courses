import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ParallelEvaluator {
    suspend fun run(task: Task, n: Int, context: CoroutineContext) {
        coroutineScope {
            val tasks = List(n) {
                async(context) {
                    try {
                        task.run(it)
                    } catch (e: Throwable) {
                        throw TaskEvaluationException(e)
                    }
                }
            }
            tasks.awaitAll()
        }
    }
}
