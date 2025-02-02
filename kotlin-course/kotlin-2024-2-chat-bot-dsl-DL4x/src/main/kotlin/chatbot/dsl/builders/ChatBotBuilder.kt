package chatbot.dsl.builders

import chatbot.api.*
import chatbot.bot.Bot
import chatbot.bot.MessageHandler
import chatbot.dsl.annotations.ChatBotDsl

@ChatBotDsl
class ChatBotBuilder(private val client: Client) {
    private var logLevel: LogLevel = LogLevel.ERROR
    private var contextManager: ChatContextsManager? = null
    private val messageHandlers = mutableListOf<MessageHandler<ChatContext?>>()

    fun use(logLevel: LogLevel) {
        this.logLevel = logLevel
    }

    operator fun LogLevel.unaryPlus() = use(this)

    fun use(contextManager: ChatContextsManager?) {
        this.contextManager = contextManager
    }

    fun behaviour(init: BehaviourBuilder<ChatContext?>.() -> Unit) {
        messageHandlers += BehaviourBuilder({ true }, { it }).apply(init).messageHandlers
    }

    fun create(): ChatBot {
        return Bot(
            logLevel = logLevel,
            messageHandlers = messageHandlers,
            contextManager = contextManager,
            client = client,
        )
    }
}
