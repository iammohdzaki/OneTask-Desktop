package com.one.task.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.one.task.CustomVerticalScrollbar
import com.one.task.domain.*
import com.one.task.presentation.ui.components.blocks.BlockRenderer
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainEditorCanvas(
    pageId: String,
    pageTitle: String,
    pageDescription: String?,
    tags: List<String>,
    blocks: List<ContentBlock>,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String?) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onUpdateBlock: (ContentBlock) -> Unit,
    onAddBlock: (ContentBlock) -> Unit,
    onDeleteBlock: (String) -> Unit
) {
    val listState = rememberLazyListState()
    
    // Local buffers for text fields to prevent typing lag/race conditions with the DB
    var localTitle by remember(pageId) { mutableStateOf(pageTitle) }
    var localDescription by remember(pageId) { mutableStateOf(pageDescription ?: "") }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                        .padding(horizontal = 32.dp)
                        .padding(top = 16.dp, bottom = 48.dp)
                        .fillMaxWidth()
                ) {
                    // ── Editable Page Title ───────────────────────────────────
                    BasicTextField(
                        value = localTitle,
                        onValueChange = {
                            localTitle = it
                            onTitleChange(it)
                        },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        decorationBox = { innerTextField ->
                            if (localTitle.isEmpty()) {
                                Text(
                                    text = stringResource(Res.string.hint_page_title),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )

                    // ── Page Description ──────────────────────────────────────
                    BasicTextField(
                        value = localDescription,
                        onValueChange = {
                            localDescription = it
                            onDescriptionChange(it.takeIf { it.isNotBlank() })
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        decorationBox = { innerTextField ->
                            if (localDescription.isEmpty()) {
                                Text(
                                    text = "Add a description...",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )

                    // ── Tag Row ───────────────────────────────────────────────
                    TagRow(
                        tags = tags,
                        onAddTag = onAddTag,
                        onRemoveTag = onRemoveTag
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // ── Block List ────────────────────────────────────────────
                    blocks.forEach { block ->
                        BlockRenderer(
                            block = block,
                            onUpdate = onUpdateBlock,
                            onDelete = onDeleteBlock
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // ── Floating Block Insert Toolbar ──────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            BlockInsertToolbar(
                currentBlockCount = blocks.size,
                onAddBlock = onAddBlock
            )
        }

        CustomVerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(end = 4.dp),
            state = listState
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tag Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TagRow(
    tags: List<String>,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit
) {
    var isAddingTag by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf("") }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Existing tags as chips
        tags.forEach { tag ->
            TagChip(tag = tag, onRemove = { onRemoveTag(tag) })
        }

        // Add-tag control
        if (isAddingTag) {
            BasicTextField(
                value = tagInput,
                onValueChange = { tagInput = it },
                textStyle = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                modifier = Modifier
                    .widthIn(min = 80.dp, max = 200.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Enter) {
                            if (tagInput.isNotBlank()) {
                                onAddTag(tagInput.trim())
                            }
                            tagInput = ""
                            isAddingTag = false
                            true
                        } else if (keyEvent.key == Key.Escape) {
                            tagInput = ""
                            isAddingTag = false
                            true
                        } else {
                            false
                        }
                    },
                decorationBox = { innerTextField ->
                    if (tagInput.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.hint_add_tag),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                    innerTextField()
                }
            )
        } else {
            // "+" button to open tag input
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable { isAddingTag = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun TagChip(tag: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "#$tag",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.btn_remove_tag),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Block Insert Toolbar
// ─────────────────────────────────────────────────────────────────────────────

private data class InsertEntry(
    val label: String,
    val icon: ImageVector,
    val makeBlock: (id: String, sortOrder: Int) -> ContentBlock
)

@Composable
private fun BlockInsertToolbar(
    currentBlockCount: Int,
    onAddBlock: (ContentBlock) -> Unit
) {
    val nextOrder = currentBlockCount

    val blockTypes = listOf(
        InsertEntry(stringResource(Res.string.block_type_text), Icons.AutoMirrored.Filled.Subject)
            { id, order -> TextBlock(id, order, "") },
        InsertEntry(stringResource(Res.string.block_type_heading), Icons.Default.Title)
            { id, order -> HeadingBlock(id, order, 1, "") },
        InsertEntry(stringResource(Res.string.block_type_task), Icons.Default.CheckBox)
            { id, order -> CheckboxBlock(id, order, "", false) },
        InsertEntry(stringResource(Res.string.block_type_divider), Icons.Default.HorizontalRule)
            { id, order -> DividerBlock(id, order) },
        InsertEntry(stringResource(Res.string.block_type_image), Icons.Default.Image)
            { id, order -> ImageBlock(id, order, "") },
        InsertEntry(stringResource(Res.string.block_type_table), Icons.Default.TableChart)
            { id, order -> TableBlock(id, order, "", 3, 3, List(3) { List(3) { "" } }) },
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        blockTypes.forEach { entry ->
            BlockTypeButton(
                label = entry.label,
                icon = entry.icon,
                onClick = {
                    val newId = generateBlockId()
                    onAddBlock(entry.makeBlock(newId, nextOrder))
                }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BlockTypeButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isHovered) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f) else Color.Transparent)
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false }
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isHovered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = if (isHovered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Generate a simple unique id for new blocks inside the composable layer. */
private fun generateBlockId(): String {
    val chars = ('a'..'z') + ('0'..'9')
    return (1..12).map { chars.random() }.joinToString("")
}
