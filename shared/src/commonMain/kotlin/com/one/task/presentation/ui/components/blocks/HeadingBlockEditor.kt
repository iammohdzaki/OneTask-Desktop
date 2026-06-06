package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.one.task.domain.HeadingBlock
import com.one.task.presentation.ui.utils.RichTextVisualTransformation
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun HeadingBlockEditor(
    block: HeadingBlock, 
    onUpdate: (HeadingBlock) -> Unit,
    isActive: Boolean = false,
    onFocus: () -> Unit = {},
    onSelectionChanged: (TextFieldValue) -> Unit = {},
    formatEvent: Pair<String, Long>? = null,
    onFormatApplied: () -> Unit = {}
) {
    var textFieldValue by remember(block.id) { mutableStateOf(TextFieldValue(text = block.text)) }
    val focusRequester = remember { FocusRequester() }

    // Sync external changes if needed
    LaunchedEffect(block.text) {
        if (block.text != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = block.text)
        }
    }

    // Report initial selection or when it changes internally
    LaunchedEffect(textFieldValue) {
        if (isActive) {
            onSelectionChanged(textFieldValue)
        }
    }

    LaunchedEffect(formatEvent) {
        if (isActive && formatEvent != null) {
            val marker = formatEvent.first
            val selStart = textFieldValue.selection.start
            val selEnd = textFieldValue.selection.end
            val min = minOf(selStart, selEnd)
            val max = maxOf(selStart, selEnd)
            
            val text = textFieldValue.text
            
            val isAlreadyFormatted = min >= marker.length && max <= text.length - marker.length && 
                                     text.substring(min - marker.length, min) == marker && 
                                     text.substring(max, max + marker.length) == marker
            
            val newValue = if (isAlreadyFormatted) {
                val newText = text.substring(0, min - marker.length) + text.substring(min, max) + text.substring(max + marker.length)
                TextFieldValue(newText, TextRange(min - marker.length, max - marker.length))
            } else if (min == max) {
                val newText = text.substring(0, min) + marker + marker + text.substring(min)
                TextFieldValue(newText, TextRange(min + marker.length))
            } else {
                val newText = text.substring(0, min) + marker + text.substring(min, max) + marker + text.substring(max)
                TextFieldValue(newText, TextRange(min + marker.length, max + marker.length))
            }
            
            textFieldValue = newValue
            onUpdate(block.copy(text = newValue.text))
            onFormatApplied()
            
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                // Focus request failed
            }
        }
    }

    val textStyle = when (block.level) {
        1 -> MaterialTheme.typography.displaySmall.copy(color = MaterialTheme.colorScheme.onSurface)
        2 -> MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurface)
        else -> MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onUpdate(block.copy(text = it.text))
                onSelectionChanged(it)
            },
            textStyle = textStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            visualTransformation = RichTextVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { if (it.isFocused) onFocus() },
            decorationBox = { innerTextField ->
                if (textFieldValue.text.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.hint_heading),
                        style = textStyle.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    )
                }
                innerTextField()
            }
        )
    }
}
