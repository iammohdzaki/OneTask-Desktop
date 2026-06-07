package com.one.task.presentation.ui.components.blocks

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.zIndex
import com.one.task.domain.CheckboxBlock
import com.one.task.domain.ContentBlock
import com.one.task.domain.DividerBlock
import com.one.task.domain.HeadingBlock
import com.one.task.domain.ImageBlock
import com.one.task.domain.LinkBlock
import com.one.task.domain.TableBlock
import com.one.task.domain.TextBlock
import com.one.task.domain.currentTimeMillis
import com.one.task.presentation.ui.Motion
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * Carries a format command with the selection frozen at button-click time.
 * Must match the definition in MainEditorCanvas (same package-level concept,
 * re-declared here for block-layer access without circular coupling).
 */
data class FormatCommand(
    val marker: String,
    val selectionStart: Int,
    val selectionEnd: Int,
    val id: Long = currentTimeMillis()
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BlockRenderer(
    block: ContentBlock,
    onUpdate: (ContentBlock) -> Unit,
    onDelete: ((String) -> Unit)? = null,
    isActive: Boolean = false,
    onFocus: () -> Unit = {},
    onSelectionChanged: (TextFieldValue) -> Unit = {},
    formatCommand: FormatCommand? = null,
    onFormatApplied: () -> Unit = {},
    isDragging: Boolean = false,
    onDragStart: () -> Unit = {},
    onDragDelta: (Float) -> Unit = {},
    onDragEnd: () -> Unit = {}
) {
    var isHovered by remember { mutableStateOf(false) }

    // Animate elevation when dragging
    val elevation by animateFloatAsState(
        targetValue = if (isDragging) 8f else 0f,
        animationSpec = Motion.Spec.springStandard()
    )
    val alpha by animateFloatAsState(
        targetValue = if (isDragging) 0.85f else 1f,
        animationSpec = Motion.Spec.standard()
    )
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.02f else 1f,
        animationSpec = Motion.Spec.springBouncy()
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .zIndex(if (isDragging) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .then(
                if (elevation > 0f) Modifier.shadow(elevation.dp, MaterialTheme.shapes.small)
                else Modifier
            )
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Drag handle — visible only on hover, triggers drag gesture
        Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = stringResource(Res.string.content_desc_drag_handle),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(20.dp)
                .alpha(if (isHovered || isDragging) 0.8f else 0f)
                .pointerInput(block.id) {
                    detectDragGestures(
                        onDragStart = { onDragStart() },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onDragDelta(dragAmount.y)
                        }
                    )
                }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            when (block) {
                is TextBlock     -> TextBlockEditor(block, onUpdate, isActive, onFocus, onSelectionChanged, formatCommand, onFormatApplied)
                is CheckboxBlock -> CheckboxBlockEditor(block, onUpdate, isActive, onFocus, onSelectionChanged, formatCommand, onFormatApplied)
                is ImageBlock    -> ImageBlockEditor(block) { onUpdate(it) }
                is TableBlock    -> TableBlockEditor(block) { onUpdate(it) }
                is HeadingBlock  -> HeadingBlockEditor(block, onUpdate, isActive, onFocus, onSelectionChanged, formatCommand, onFormatApplied)
                is DividerBlock  -> DividerBlockEditor(block)
                is LinkBlock     -> LinkBlockEditor(block) { onUpdate(it) }
            }
        }

        // Delete button — visible only on hover
        if (onDelete != null) {
            IconButton(
                onClick = { onDelete(block.id) },
                modifier = Modifier
                    .size(28.dp)
                    .alpha(if (isHovered && !isDragging) 1f else 0f)
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
