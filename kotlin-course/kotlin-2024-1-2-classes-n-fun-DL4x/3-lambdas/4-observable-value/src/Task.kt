interface Value<T> {
    val value: T
    fun observe(callable: (T) -> Unit): Cancellable
}

fun interface Cancellable {
    fun cancel()
}

class MutableValue<T>(initial: T) : Value<T> {
    private val observers: MutableSet<(T) -> Unit> = mutableSetOf()

    override var value = initial
        set(update) {
            field = update
            observers.forEach { it(field) }
        }

    override fun observe(callable: (T) -> Unit): Cancellable {
        callable(value)
        observers.add(callable)
        return Cancellable { observers.remove(callable) }
    }
}
