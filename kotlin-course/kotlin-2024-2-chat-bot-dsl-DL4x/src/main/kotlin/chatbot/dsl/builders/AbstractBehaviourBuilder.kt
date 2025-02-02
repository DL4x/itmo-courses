package chatbot.dsl.builders

import chatbot.api.ChatContext
import chatbot.api.Message
import chatbot.bot.MessageHandler
import chatbot.bot.MessageProcessor
import chatbot.dsl.annotations.ChatBotDsl

@ChatBotDsl
abstract class AbstractBehaviourBuilder<C : ChatContext?>(
    private val predicate: (C?) -> Boolean,
    private val reformatMessageHandler: (MessageHandler<C>) -> MessageHandler<ChatContext?>,
) {
    val messageHandlers = mutableListOf<MessageHandler<ChatContext?>>()

    fun onCommand(
        command: String,
        processor: MessageProcessor<C>,
    ) {
        onMessagePrefix("/$command", processor)
    }

    fun onMessage(
        predicate: (Message) -> Boolean,
        processor: MessageProcessor<C>,
    ) {
        val initialMessageHandler = MessageHandler(
            { message, context -> predicate(message) && predicate(context) },
            processor,
        )
        messageHandlers.add(reformatMessageHandler(initialMessageHandler))
    }

    fun onMessagePrefix(
        prefix: String,
        processor: MessageProcessor<C>,
    ) {
        onMessage({ it.text.startsWith(prefix) }, processor)
    }

    fun onMessageContains(
        text: String,
        processor: MessageProcessor<C>,
    ) {
        onMessage({ it.text.contains(text) }, processor)
    }

    fun onMessage(
        messageTextExactly: String,
        processor: MessageProcessor<C>,
    ) {
        onMessage({ it.text == messageTextExactly }, processor)
    }

    fun onMessage(
        processor: MessageProcessor<C>,
    ) {
        onMessage({ true }, processor)
    }
}
