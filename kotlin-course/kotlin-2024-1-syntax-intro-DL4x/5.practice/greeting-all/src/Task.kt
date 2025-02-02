fun greet(name: String): String {
    return "Hello, $name!"
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(greet(readlnOrNull() ?: "Anonymous"))
        return
    }
    args.forEach { println(greet(it)) }
}
