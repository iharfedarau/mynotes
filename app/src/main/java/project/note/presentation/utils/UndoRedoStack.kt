package project.note.presentation.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

typealias UndoRedoStackActionListener = (TextFieldValue) -> Unit

class UndoRedoStack {
    private var stack = mutableListOf<TextFieldValue>()
    private var stackPos = -1
    private var actionListeners = mutableListOf<UndoRedoStackActionListener>()

    var canUndo by mutableStateOf(false)
        private set

    var canRedo by mutableStateOf(false)
        private set

    fun setInitialValue(initialValue: String) {
        if (stackPos == -1) {
            stack = mutableListOf(TextFieldValue(initialValue, TextRange(initialValue.length)))
            stackPos = 0
        }
    }

    fun addListener(listener: UndoRedoStackActionListener) {
        actionListeners.add(listener)
    }

    fun undo() {
        if (canUndo) {
            stackPos--
            updateState(stack[stackPos])
        }
    }

    fun redo() {
        if (canRedo) {
            stackPos++
            updateState(stack[stackPos])
        }
    }

    fun push(value: TextFieldValue) {
        if (stack[stackPos].text == value.text) {
            stack[stackPos] = value
            updateState(value)
            return
        }

        stackPos++

        if (stack.size > stackPos) {
            stack.add(stackPos, value)
            stack.subList(stackPos + 1, stack.size - 1).clear()
        } else {
            stack.add(value)
        }

        updateState(value)
    }

    private fun updateState(value: TextFieldValue) {
        canUndo = stackPos >= 1
        canRedo = stack.isNotEmpty() && stackPos < stack.size - 1

        actionListeners.forEach {
            it(value)
        }
    }
}