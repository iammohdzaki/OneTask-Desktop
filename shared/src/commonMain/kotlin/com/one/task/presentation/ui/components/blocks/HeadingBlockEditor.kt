package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.one.task.domain.HeadingBlock
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun HeadingBlockEditor(block: HeadingBlock, onUpdate: (HeadingBlock) -> Unit) {
    var localText by remember(block.id) { mutableStateOf(block.text) }

    val textStyle = when (block.level) {
        1 -> MaterialTheme.typography.displaySmall.copy(color = MaterialTheme.colorScheme.onSurface)
        2 -> MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurface)
        else -> MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface)
    }

    BasicTextField(
        value = localText,
        onValueChange = {
            localText = it
            onUpdate(block.copy(text = it))
        },
        textStyle = textStyle,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        decorationBox = { innerTextField ->
            if (localText.isEmpty()) {
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
