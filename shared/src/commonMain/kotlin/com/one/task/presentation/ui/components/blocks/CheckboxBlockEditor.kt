package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.task.domain.CheckboxBlock
import com.one.task.presentation.ui.components.hoverableBackground
import com.one.task.presentation.ui.utils.FormatEngine
import com.one.task.presentation.ui.utils.RichTextVisualTransformation
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckboxBlockEditor(
    block: CheckboxBlock,
    onUpdate: (CheckboxBlock) -> Unit,
    isActive: Boolean = false,
    onFocus: () -> Unit = {},
    onSelectionChanged: (TextFieldValue) -> Unit = {},
    formatCommand: FormatCommand? = null,
    onFormatApplied: () -> Unit = {}
) {
    var textFieldValue by remember(block.id) { mutableStateOf(TextFieldValue(text = block.text)) }
    var tagFieldValue by remember(block.id) { mutableStateOf(TextFieldValue(text = block.tag ?: "")) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(block.text) {
        if (block.text != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = block.text)
        }
    }
    LaunchedEffect(block.tag) {
        val newTag = block.tag ?: ""
        if (newTag != tagFieldValue.text) {
            tagFieldValue = tagFieldValue.copy(text = newTag)
        }
    }

    // Report selection changes upward
    LaunchedEffect(textFieldValue) {
        if (isActive) onSelectionChanged(textFieldValue)
    }

    // Apply format command — keyed on id so rapid same-marker clicks always fire
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .hoverableBackground(hoverColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = block.isChecked,
                onCheckedChange = { onUpdate(block.copy(isChecked = it)) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outlineVariant,
                    checkmarkColor = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    onUpdate(block.copy(text = it.text))
                    onSelectionChanged(it)
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (block.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (block.isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                visualTransformation = RichTextVisualTransformation(),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { if (it.isFocused) onFocus() },
                decorationBox = { innerTextField ->
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.hint_type_task), 
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                        )
                    }
                    innerTextField()
                }
            )
            
            if (block.tag != null || isActive) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                        .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    if (isActive) {
                        BasicTextField(
                            value = tagFieldValue,
                            onValueChange = {
                                tagFieldValue = it
                                onUpdate(block.copy(tag = it.text.takeIf { t -> t.isNotBlank() }))
                            },
                            textStyle = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.error),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.error),
                            modifier = Modifier.width(IntrinsicSize.Min).defaultMinSize(minWidth = 30.dp),
                            decorationBox = { innerTextField ->
                                if (tagFieldValue.text.isEmpty()) {
                                    Text(
                                        text = "TAG",
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp)
                                    )
                                }
                                innerTextField()
                            }
                        )
                    } else if (block.tag != null) {
                        Text(
                            text = block.tag!!.uppercase(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp)
                        )
                    }
                }
            }
            
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(Res.string.content_desc_more),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp).alpha(0.5f)
            )
        }
    }
}
