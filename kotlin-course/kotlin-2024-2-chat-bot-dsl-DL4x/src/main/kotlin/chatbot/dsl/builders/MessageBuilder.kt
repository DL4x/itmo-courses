package chatbot.dsl.builders

import chatbot.api.Keyboard
import chatbot.api.Message
import chatbot.api.MessageId
import chatbot.dsl.annotations.ChatBotDsl

@ChatBotDsl
class MessageBuilder(val message: Message) {
    var text: String = ""
    var replyTo: MessageId? = null
    var keyboard: Keyboard? = null

    fun removeKeyboard() {
        keyboard = Keyboard.Remove
    }

    fun withKeyboard(init: KeyboardBuilder.() -> Unit) {
        keyboard = KeyboardBuilder().apply(init).create()
    }

    private fun isEmptyMessage(): Boolean =
        text.isEmpty() && keyboard == null

    private fun isEmptyKeyboard(): Boolean =
        (keyboard as? Keyboard.Markup)?.keyboard?.all { it.isEmpty() } == true

    fun isEmpty(): Boolean = isEmptyMessage() || isEmptyKeyboard()
}
