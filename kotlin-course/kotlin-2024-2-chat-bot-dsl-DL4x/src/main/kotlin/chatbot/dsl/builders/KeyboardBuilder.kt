package chatbot.dsl.builders

import chatbot.api.Keyboard
import chatbot.api.Keyboard.Button
import chatbot.dsl.annotations.ChatBotDsl

@ChatBotDsl
class KeyboardBuilder {
    var oneTime: Boolean = false
    var keyboard: MutableList<MutableList<Button>> = mutableListOf()

    @ChatBotDsl
    inner class RowKeyboardBuilder {
        val buttons = mutableListOf<Button>()

        fun button(text: String) {
            buttons.add(Button(text))
        }

        operator fun String.unaryMinus() = button(this)
    }

    fun row(init: RowKeyboardBuilder.() -> Unit) {
        keyboard += RowKeyboardBuilder().apply(init).buttons
    }

    fun create(): Keyboard {
        return Keyboard.Markup(oneTime, keyboard)
    }
}
