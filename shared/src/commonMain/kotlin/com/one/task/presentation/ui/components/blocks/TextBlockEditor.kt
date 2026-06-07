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
import com.one.task.domain.TextBlock
import com.one.task.presentation.ui.utils.FormatEngine
import com.one.task.presentation.ui.utils.RichTextVisualTransformation
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun TextBlockEditor(
    block: TextBlock,
    onUpdate: (TextBlock) -> Unit,
    isActive: Boolean = false,
    onFocus: () -> Unit = {},
    onSelectionChanged: (TextFieldValue) -> Unit = {},
    formatCommand: FormatCommand? = null,
    onFormatApplied: () -> Unit = {}
) {
    var textFieldValue by remember(block.id) { mutableStateOf(TextFieldValue(text = block.text)) }
    val focusRequester = remember { FocusRequester() }

    // Sync external text changes (e.g. from DB) without resetting the cursor
    LaunchedEffect(block.text) {
        if (block.text != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = block.text)
        }
    }

    // Report selection changes upward so the toolbar can compute formatting state
    LaunchedEffect(textFieldValue) {
        if (isActive) onSelectionChanged(textFieldValue)
    }

    // Apply format command — uses the frozen selection captured at click time
    LaunchedEffect(formatCommand?.id) {
        if (isActive && formatCommand != null) {
            val frozenSelection = TextRange(formatCommand.selectionStart, formatCommand.selectionEnd)
            val newValue = FormatEngine.applyFormat(textFieldValue, formatCommand.marker, frozenSelection)
            textFieldValue = newValue
            onUpdate(block.copy(text = newValue.text))
            onFormatApplied()
            try { focusRequester.requestFocus() } catch (_: Exception) {}
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onUpdate(block.copy(text = it.text))
                onSelectionChanged(it)
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            visualTransformation = RichTextVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { if (it.isFocused) onFocus() },
            decorationBox = { innerTextField ->
                if (textFieldValue.text.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.hint_type_description),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
                innerTextField()
            }
        )
    }
}
