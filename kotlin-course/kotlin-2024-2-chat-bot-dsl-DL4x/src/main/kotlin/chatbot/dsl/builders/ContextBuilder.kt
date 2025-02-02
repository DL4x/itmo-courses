package chatbot.dsl.builders

import chatbot.api.ChatContext
import chatbot.bot.MessageHandler
import chatbot.dsl.annotations.ChatBotDsl

@ChatBotDsl
class ContextBuilder<C : ChatContext?>(
    predicate: (C?) -> Boolean,
    reformatMessageHandler: (MessageHandler<C>) -> MessageHandler<ChatContext?>,
) : AbstractBehaviourBuilder<C>(predicate, reformatMessageHandler)
