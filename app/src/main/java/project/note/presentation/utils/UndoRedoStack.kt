package project.note.presentation.utils

import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class UndoRedoStack  {
    var stack = mutableListOf<TextFieldValue>()
    var stackPos = - 1

    private var actionListener: ((data: TextFieldValue, canUndo: Boolean, canRedo: Boolean) -> Unit)? = null

    fun setInitialValue(initialValue: String) {
        if (stackPos == -1) {
            stack = mutableListOf(TextFieldValue(initialValue, TextRange(initialValue.length)))
            stackPos = 0
        }
    }

    fun setListener(listener: (data: TextFieldValue, canUndo: Boolean, canRedo: Boolean) -> Unit) {
        actionListener = listener
    }

    fun undo() {
        if (canUndo()) {
            stackPos--
            actionListener?.let {
                it(stack[stackPos], canUndo(), canRedo())
            }
        }
    }

    fun redo() {
        if (canRedo()) {
            stackPos++
            actionListener?.let {
                it(stack[stackPos], canUndo(), canRedo())
            }
        }
    }

    fun push(value: TextFieldValue) {
        if (stack[stackPos].text == value.text) {
            stack[stackPos] = value
            actionListener?.let {
                it(value, canUndo(), canRedo())
            }
            return
        }

        stackPos++

        if (stack.size > stackPos) {
            stack.add(stackPos, value)
            stack.subList(stackPos + 1, stack.size - 1).clear()
        } else {
            stack.add(value)
        }

        actionListener?.let {
            it(value, canUndo(), canRedo())
        }
    }

    private fun canUndo(): Boolean {
        return stackPos >= 1
    }

    private fun canRedo(): Boolean {
        return stack.isNotEmpty() && stackPos < stack.size - 1
    }

    companion object {
        val Saver = run {
            val stackKey = "Stack"
            val stackPosKey = "StackPos"
            mapSaver(
                save = {
                    mapOf(stackKey to it.stack, stackPosKey to it.stackPos)
                       },
                restore = {
                    val stack = UndoRedoStack()
                    stack.stack = it[stackKey] as MutableList<TextFieldValue>
                    stack.stackPos = it[stackPosKey] as Int
                    stack
                }
            )
        }
    }
}