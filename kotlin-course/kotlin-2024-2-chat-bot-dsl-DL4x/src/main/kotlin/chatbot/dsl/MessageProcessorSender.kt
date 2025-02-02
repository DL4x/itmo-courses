package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.ChatId
import chatbot.bot.MessageProcessorContext
import chatbot.dsl.builders.MessageBuilder

fun <C : ChatContext?> MessageProcessorContext<C>.sendMessage(
    chatId: ChatId,
    init: MessageBuilder.() -> Unit,
) {
    val messageBuilder = MessageBuilder(message).apply(init)
    if (!messageBuilder.isEmpty()) {
        client.sendMessage(
            chatId = chatId,
            text = messageBuilder.text,
            keyboard = messageBuilder.keyboard,
            replyMessageId = messageBuilder.replyTo,
        )
    }
}

fun <C : ChatContext?> MessageProcessorContext<C>.sendMessage(
    chatId: ChatId,
    messageText: String,
) = sendMessage(chatId) {
    text = messageText
}
