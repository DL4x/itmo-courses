package chatbot.dsl

import chatbot.api.ChatBot
import chatbot.api.Client
import chatbot.dsl.builders.ChatBotBuilder

fun chatBot(client: Client, init: ChatBotBuilder.() -> Unit): ChatBot {
    return ChatBotBuilder(client).apply(init).create()
}
