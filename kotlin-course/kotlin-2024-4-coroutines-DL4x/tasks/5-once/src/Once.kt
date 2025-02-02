import kotlinx.coroutines.sync.Mutex

class Once {
    private val mutex = Mutex()

    fun run(block: () -> Unit) {
        if (mutex.isLocked) {
            return
        }
        if (mutex.tryLock()) {
            block()
        }
    }
}
