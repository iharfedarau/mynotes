package project.note.presentation.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class UndoRedoStack(
    initialValue: String,
    listener: (data: TextFieldValue?, canUndo: Boolean, canRedo: Boolean) -> Unit
) {
    private val stack = mutableListOf<TextFieldValue>()
    private var stackPos = -1
    private var actionListener: (data: TextFieldValue?, canUndo: Boolean, canRedo: Boolean) -> Unit =
        listener
    private val initial: String = initialValue

    private fun canUndo(): Boolean {
        return stackPos >= 0
    }

    private fun canRedo(): Boolean {
        return stack.isNotEmpty() && stackPos < stack.size - 1
    }

    fun undo() {
        if (canUndo()) {
            stackPos--
            if (stackPos >= 0) {
                actionListener(stack[stackPos], canUndo(), canRedo())
            } else {
                actionListener(
                    TextFieldValue(initial, TextRange(initial.length)),
                    canUndo(),
                    canRedo()
                )
            }
        } else {
            actionListener(if (stackPos >= 0) stack[stackPos] else null, canUndo(), canRedo())
        }
    }

    fun redo() {
        if (canRedo()) {
            stackPos++
            actionListener(stack[stackPos], canUndo(), canRedo())
        } else {
            actionListener(if (stackPos >= 0) stack[stackPos] else null, canUndo(), canRedo())
        }
    }

    fun push(value: TextFieldValue) {
       if (stackPos >= 0 && stack[stackPos].text == value.text && stack[stackPos].selection == value.selection) {
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