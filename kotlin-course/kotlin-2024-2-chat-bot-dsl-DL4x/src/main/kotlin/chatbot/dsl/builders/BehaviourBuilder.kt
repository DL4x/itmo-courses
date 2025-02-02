package chatbot.dsl.builders

import chatbot.api.*
import chatbot.bot.MessageHandler
import chatbot.bot.MessageProcessorContext
import chatbot.dsl.annotations.ChatBotDsl

@ChatBotDsl
class BehaviourBuilder<C : ChatContext?>(
    predicate: (C?) -> Boolean,
    reformatMessageHandler: (MessageHandler<C>) -> MessageHandler<ChatContext?>,
) : AbstractBehaviourBuilder<C>(predicate, reformatMessageHandler) {
    inline fun <reified T : ChatContext?> into(init: ContextBuilder<T>.() -> Unit) =
        intoImpl<T>(init, { it is T }, messageHandlers)

    inline infix fun <reified T : ChatContext?> T.into(init: ContextBuilder<T>.() -> Unit) =
        intoImpl<T>(init, { it == this }, messageHandlers)

    companion object {
        inline fun <reified T : ChatContext?> intoImpl(
            init: ContextBuilder<T>.() -> Unit,
            crossinline predicate: (T?) -> Boolean,
            handlers: MutableList<MessageHandler<ChatContext?>>,
        ) {
            handlers += ContextBuilder<T>({ predicate(it) }, getReformater()).apply(init).messageHandlers
        }

        inline fun <reified T : ChatContext?> getReformater() =
            { handler: MessageHandler<T> ->
                MessageHandler<ChatContext?>(
                    { message, context -> context is T && handler.predicate(message, context) },
                    { handler.processor(MessageProcessorContext(message, client, context as T, setContext)) },
                )
            }
    }
}
