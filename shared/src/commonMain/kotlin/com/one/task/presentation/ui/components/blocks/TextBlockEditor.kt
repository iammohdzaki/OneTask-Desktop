package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.one.task.domain.TextBlock
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun TextBlockEditor(block: TextBlock, onUpdate: (TextBlock) -> Unit) {
    BasicTextField(
        value = block.text,
        onValueChange = { onUpdate(block.copy(text = it)) },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        decorationBox = { innerTextField ->
            if (block.text.isEmpty()) {
                Text(
                    text = stringResource(Res.string.hint_type_description), 
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                )
            }
            innerTextField()
        }
    )
}
