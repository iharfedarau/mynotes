package project.note.presentation.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class UndoRedoStack(
    initialValue: String,
    listener: (data: TextFieldValue, canUndo: Boolean, canRedo: Boolean) -> Unit
) {
    private val stack = mutableListOf(TextFieldValue(initialValue, TextRange(initialValue.length)))
    private var stackPos = stack.size - 1
    private var actionListener: (data: TextFieldValue, canUndo: Boolean, canRedo: Boolean) -> Unit =
        listener

    private fun canUndo(): Boolean {
        return stackPos >= 1
    }

    private fun canRedo(): Boolean {
        return stack.isNotEmpty() && stackPos < stack.size - 1
    }

    fun undo() {
        if (canUndo()) {
            stackPos--
            actionListener(stack[stackPos], canUndo(), canRedo())
        }
    }

    fun redo() {
        if (canRedo()) {
            stackPos++
            actionListener(stack[stackPos], canUndo(), canRedo())
        }
    }

    fun push(value: TextFieldValue) {
        if (stack[stackPos].text == value.text) {
            stack[stackPos] = value
            actionListener(value, canUndo(), canRedo())
            return
        }

        stackPos++

        if (stack.size > stackPos) {
            stack.add(stackPos, value)
            stack.subList(stackPos + 1, stack.size - 1).clear()
        } else {
            stack.add(value)
        }

        actionListener(value, canUndo(), canRedo())
    }
}