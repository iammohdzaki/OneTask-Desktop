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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.task.domain.CheckboxBlock
import com.one.task.presentation.ui.components.hoverableBackground
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckboxBlockEditor(block: CheckboxBlock, onUpdate: (CheckboxBlock) -> Unit) {
    var localText by remember(block.id) { mutableStateOf(block.text) }

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
            value = localText,
            onValueChange = {
                localText = it
                onUpdate(block.copy(text = it))
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textDecoration = if (block.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                color = if (block.isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (localText.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.hint_type_task), 
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    )
                }
                innerTextField()
            }
        )
        
        if (block.tag != null) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                    .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = block.tag!!.uppercase(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp)
                )
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
