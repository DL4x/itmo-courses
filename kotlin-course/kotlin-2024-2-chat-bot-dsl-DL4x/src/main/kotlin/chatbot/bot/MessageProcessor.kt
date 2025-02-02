package chatbot.bot

import chatbot.api.ChatContext
import chatbot.api.Client
import chatbot.api.Message
import chatbot.dsl.annotations.ChatBotDsl

@ChatBotDsl
class MessageProcessorContext<C : ChatContext?>(
    val message: Message,
    val client: Client,
    val context: C,
    val setContext: (c: ChatContext?) -> Unit,
)

typealias MessageProcessor<C> = MessageProcessorContext<C>.() -> Unit
