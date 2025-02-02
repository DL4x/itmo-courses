package turingmachine

class TuringMachine(
    private val startingState: String,
    private val acceptedState: String,
    private val rejectedState: String,
    transitions: Collection<TransitionFunction>,
) {
    class Snapshot(var state: String, val tape: Tape) {
        fun applyTransition(transition: Transition): Snapshot {
            state = transition.newState
            tape.applyTransition(transition.newSymbol, transition.move)
            return this
        }

        fun copy(): Snapshot {
            return Snapshot(state, tape.copy())
        }

        override fun equals(other: Any?): Boolean =
            when {
                other is Snapshot -> {
                    state == other.state && tape == other.tape
                }
                else -> false
            }

        override fun hashCode(): Int {
            var result = state.hashCode()
            result = 31 * result + tape.hashCode()
            return result
        }

        override fun toString(): String =
            """
            State: $state
            $tape
            """.trimIndent()
    }

    class Tape(initialString: String) {
        var position: Int = 0
        val content: MutableList<Char> = initialString
            .toMutableList()
            .ifEmpty { mutableListOf(BLANK) }

        private fun removeIfRedundantBlankAt(index: Int): Boolean {
            if (position != index && content[index] == BLANK) {
                content.removeAt(index)
                return true
            }
            return false
        }

        private fun applyStayTransition(char: Char) {
            content[position] = char
        }

        private fun applyLeftTransition(char: Char) {
            content[position--] = char
            if (position == -1) {
                position = 0
                content.add(0, BLANK)
            }
            removeIfRedundantBlankAt(content.size - 1)
        }

        private fun applyRightTransition(char: Char) {
            content[position++] = char
            if (position == content.size) {
                content.add(BLANK)
            }
            if (removeIfRedundantBlankAt(0)) position--
        }

        fun applyTransition(char: Char, move: TapeTransition): Tape {
            when (move) {
                TapeTransition.Stay -> applyStayTransition(char)
                TapeTransition.Left -> applyLeftTransition(char)
                TapeTransition.Right -> applyRightTransition(char)
            }
            return this
        }

        fun copy(): Tape {
            val initialString = content.joinToString("")
            val tape = Tape(initialString)
            tape.position = position
            return tape
        }

        override fun equals(other: Any?): Boolean =
            when {
                other is Tape -> {
                    position == other.position && content == other.content
                }
                else -> false
            }

        override fun hashCode(): Int {
            var result = position
            result = 31 * result + content.hashCode()
            return result
        }

        override fun toString(): String {
            return content.joinToString("")
        }
    }

    private val transitionsMap = HashMap<Pair<String, Char>, Transition>()

    init {
        transitions.forEach { transitionsMap[it.state to it.symbol] = it.transition }
    }

    fun initialSnapshot(input: String): Snapshot {
        val tape = Tape(input)
        return Snapshot(startingState, tape)
    }

    fun simulateStep(snapshot: Snapshot): Snapshot {
        val state = snapshot.state
        val tape = snapshot.tape
        val symbol = tape.content[tape.position]
        val transition = transitionsMap[state to symbol]
            ?: return Snapshot(rejectedState, tape)
        tape.applyTransition(transition.newSymbol, transition.move)
        return Snapshot(transition.newState, tape)
    }

    private fun isTermination(snapshot: Snapshot): Boolean =
        snapshot.state == acceptedState || snapshot.state == rejectedState

    fun simulate(initialString: String): Sequence<Snapshot> = sequence {
        var snapshot = initialSnapshot(initialString)
        yield(snapshot)
        while (!isTermination(snapshot)) {
            snapshot = simulateStep(snapshot)
            yield(snapshot)
        }
    }
}
