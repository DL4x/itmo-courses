sealed interface IntResult {
    data class Ok(val value: Int) : IntResult
    data class Error(val reason: String) : IntResult

    fun getOrDefault(default: Int): Int =
        when (this) {
            is Ok -> value
            is Error -> default
        }

    fun getOrNull(): Int? =
        when (this) {
            is Ok -> value
            is Error -> null
        }

    fun getStrict(): Int =
        when (this) {
            is Ok -> value
            is Error -> throw NoResultProvided(reason)
        }
}

class NoResultProvided(reason: String) : NoSuchElementException(reason)

fun safeRun(unsafe: () -> Int): IntResult =
    try {
        IntResult.Ok(unsafe.invoke())
    } catch (e: Exception) {
        IntResult.Error(e.message ?: "Unknown error")
    }
