package turingmachine

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.float
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.properties.Delegates

class ConsoleMachineTuring : CliktCommand() {
    companion object {
        private const val STARTING_TITLE = "start:"
        private const val ACCEPTED_TITLE = "accept:"
        private const val REJECTED_TITLE = "reject:"
        private const val BLANK_TITLE = "blank:"
    }

    private lateinit var startingState: String
    private lateinit var acceptedState: String
    private lateinit var rejectedState: String
    private var blankSymbol by Delegates.notNull<Char>()

    private val transitionsFile: File by argument()
        .file()
        .help("Input file")
    private val inputStringFileName: String? by argument()
        .optional()
        .help("Initial string")
    private val delay: Float by option()
        .float()
        .default(0.5f)
        .help("Delay between operations")
    private val auto: Boolean by option()
        .flag()
        .help("Auto steps")

    private fun getInitialString(): String {
        inputStringFileName?.let {
            try {
                val inputStringFile = File(it)
                inputStringFile
                    .bufferedReader()
                    .use { reader ->
                        return reader.readLine()
                    }
            } catch (e: FileNotFoundException) {
                System.err.println(
                    "Warning: file with input string not found or does not exist. " +
                        "Enter line in the console",
                )
            }
        }
        return readln()
    }

    private fun parseSymbol(potentialSymbol: String): Char {
        require(potentialSymbol.length == 1) {
            "Unexpected value ($potentialSymbol) for symbol"
        }
        return potentialSymbol.single()
    }

    private fun requireHeader(currentHeader: String, expectedHeader: String) =
        require(currentHeader == expectedHeader) {
            "Turing machine file must contain $expectedHeader: <value>"
        }

    private fun parseHeader(scanner: Scanner) {
        requireHeader(scanner.next(), STARTING_TITLE)
        startingState = scanner.next()
        requireHeader(scanner.next(), ACCEPTED_TITLE)
        acceptedState = scanner.next()
        requireHeader(scanner.next(), REJECTED_TITLE)
        rejectedState = scanner.next()
        requireHeader(scanner.next(), BLANK_TITLE)
        blankSymbol = parseSymbol(scanner.next())
    }

    private fun formatBlank(char: Char): Char =
        if (char == blankSymbol) BLANK else char

    private fun parseMove(potentialMove: String) =
        when (potentialMove) {
            "^" -> TapeTransition.Stay
            "<" -> TapeTransition.Left
            ">" -> TapeTransition.Right
            else -> throw IllegalArgumentException(
                "Move can be \'^\', \'<\' or \'>\', not $potentialMove",
            )
        }

    private fun parseTransitions(scanner: Scanner): Collection<TransitionFunction> {
        try {
            val transitions = mutableListOf<TransitionFunction>()
            while (scanner.hasNext()) {
                val state = scanner.next()
                val symbol = formatBlank(parseSymbol(scanner.next()))
                require(scanner.next() == "->") {
                    "Transition must contain \'->\' operator"
                }
                val newState = scanner.next()
                val newSymbol = formatBlank(parseSymbol(scanner.next()))
                val move = parseMove(scanner.next())
                transitions.add(TransitionFunction(state, symbol, move, newSymbol, newState))
            }
            return transitions
        } catch (e: NoSuchElementException) {
            throw IllegalArgumentException(
                "Unexpected end of the file. Check the correctness of the entered data",
            )
        }
    }

    private fun parseTransitionsFile(): Collection<TransitionFunction> =
        Scanner(transitionsFile).use {
            parseHeader(it)
            parseTransitions(it)
        }

    private fun printMachineStep(snapshot: TuringMachine.Snapshot) {
        println(snapshot.toString().replace(BLANK, blankSymbol))
        val position = snapshot.tape.position
        println("${" ".repeat(position)}^")
        when (snapshot.state) {
            acceptedState -> println("Accepted")
            rejectedState -> println("Rejected")
        }
    }

    private fun iterateTuringMachine(
        initialString: String,
        turingMachine: TuringMachine,
    ) {
        val turingMachineIterator = turingMachine
            .simulate(initialString)
            .iterator()
        while (turingMachineIterator.hasNext()) {
            val snapshot = turingMachineIterator.next()
            printMachineStep(snapshot)
            if (auto) {
                Thread.sleep((delay * 1000L).toLong())
                continue
            }
            readlnOrNull()
        }
    }

    override fun run() {
        try {
            val initialString = getInitialString()
            val transitions = parseTransitionsFile()
            val turingMachine = TuringMachine(
                startingState = startingState,
                acceptedState = acceptedState,
                rejectedState = rejectedState,
                transitions = transitions,
            )
            iterateTuringMachine(
                initialString = initialString,
                turingMachine = turingMachine,
            )
        } catch (e: IllegalArgumentException) {
            System.err.println(e.message)
        } catch (e: FileNotFoundException) {
            System.err.println(e.message)
        } catch (e: Exception) {
            System.err.println("Unexpected error has occurred")
        }
    }
}

fun main(args: Array<String>) = ConsoleMachineTuring().main(args)
