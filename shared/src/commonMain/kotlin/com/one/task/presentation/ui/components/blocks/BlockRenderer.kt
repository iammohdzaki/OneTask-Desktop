package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import com.one.task.domain.CheckboxBlock
import com.one.task.domain.ContentBlock
import com.one.task.domain.DividerBlock
import com.one.task.domain.HeadingBlock
import com.one.task.domain.ImageBlock
import com.one.task.domain.TableBlock
import com.one.task.domain.TextBlock
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BlockRenderer(
    block: ContentBlock,
    onUpdate: (ContentBlock) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    var isHovered by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Drag handle — visible only on hover
        Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = stringResource(Res.string.content_desc_drag_handle),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(20.dp)
                .alpha(if (isHovered) 0.6f else 0f)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            when (block) {
                is TextBlock -> TextBlockEditor(block) { onUpdate(it) }
                is CheckboxBlock -> CheckboxBlockEditor(block) { onUpdate(it) }
                is ImageBlock -> ImageBlockEditor(block) { onUpdate(it) }
                is TableBlock -> TableBlockEditor(block) { onUpdate(it) }
                is HeadingBlock -> HeadingBlockEditor(block) { onUpdate(it) }
                is DividerBlock -> DividerBlockEditor(block)
            }
        }

        // Delete button — visible only on hover
        if (onDelete != null) {
            IconButton(
                onClick = { onDelete(block.id) },
                modifier = Modifier
                    .size(28.dp)
                    .alpha(if (isHovered) 1f else 0f)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.content_desc_delete_block),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
