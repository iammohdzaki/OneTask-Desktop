package com.one.task.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.one.task.presentation.ui.Motion
import com.one.task.presentation.ui.Dimens
import com.one.task.presentation.ui.utils.FormatEngine
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.one.task.CustomVerticalScrollbar
import com.one.task.domain.*
import com.one.task.presentation.ui.components.blocks.BlockRenderer
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.one.task.presentation.ui.components.blocks.FormatCommand
import androidx.compose.material.icons.filled.Link

private data class FormattingState(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isStrike: Boolean = false
)

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
    onDeleteBlock: (String) -> Unit,
    onReorderBlocks: (List<ContentBlock>) -> Unit = {}
) {
    val listState = rememberLazyListState()

    // Local buffers for text fields to prevent typing lag/race conditions with the DB
    var localTitle by remember(pageId) { mutableStateOf(pageTitle) }
    var localDescription by remember(pageId) { mutableStateOf(pageDescription ?: "") }

    var activeBlockId by remember { mutableStateOf<String?>(null) }
    var pendingFormatCommand by remember { mutableStateOf<FormatCommand?>(null) }
    var activeSelection by remember { mutableStateOf(TextFieldValue()) }

    // ── Drag-to-reorder state ────────────────────────────────────────────────
    // Mutable local copy so we can optimistically update order during drag
    var localBlocks by remember(pageId, blocks) { mutableStateOf(blocks) }
    var draggingBlockId by remember { mutableStateOf<String?>(null) }
    var accumulatedDragY by remember { mutableStateOf(0f) }
    // Approximate row height in px — used to compute swap threshold
    val blockRowHeightPx = 56f


    val formattingState = remember(activeSelection) {
        val text = activeSelection.text
        val selection = activeSelection.selection
        if (text.isEmpty()) {
            FormattingState()
        } else {
            FormattingState(
                isBold      = FormatEngine.isFormatActive(text, selection, "**"),
                isItalic    = FormatEngine.isFormatActive(text, selection, "*"),
                isUnderline = FormatEngine.isFormatActive(text, selection, "__"),
                isStrike    = FormatEngine.isFormatActive(text, selection, "~~")
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .widthIn(max = Dimens.maxContentWidth)
                        .padding(horizontal = Dimens.spaceXXL)
                        .padding(top = Dimens.spaceM, bottom = Dimens.space3XL)
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
                            .padding(bottom = Dimens.spaceXXS),
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
                            .padding(bottom = Dimens.spaceM),
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

                }
            }

            // ── Block List ────────────────────────────────────────────
            items(localBlocks, key = { it.id }) { block ->
                val isDragging = draggingBlockId == block.id
                Box(
                    modifier = Modifier
                        .animateItem(
                            fadeInSpec = Motion.Spec.enter(),
                            fadeOutSpec = Motion.Spec.exit(),
                            placementSpec = Motion.Spec.springStandard()
                        )
                        .widthIn(max = Dimens.maxContentWidth)
                        .padding(horizontal = Dimens.spaceXL)
                        .fillMaxWidth()
                ) {
                    BlockRenderer(
                        block = block,
                        onUpdate = onUpdateBlock,
                        onDelete = onDeleteBlock,
                        isActive = activeBlockId == block.id,
                        onFocus = { activeBlockId = block.id },
                        onSelectionChanged = {
                            if (activeBlockId == block.id) {
                                activeSelection = it
                            }
                        },
                        formatCommand = if (activeBlockId == block.id) pendingFormatCommand else null,
                        onFormatApplied = { pendingFormatCommand = null },
                        isDragging = isDragging,
                        onDragStart = {
                            draggingBlockId = block.id
                            accumulatedDragY = 0f
                        },
                        onDragDelta = { deltaY ->
                            accumulatedDragY += deltaY
                            val currentIndex = localBlocks.indexOfFirst { it.id == draggingBlockId }
                            if (currentIndex == -1) return@BlockRenderer

                            // How many rows did we cross?
                            val steps = (accumulatedDragY / blockRowHeightPx).toInt()
                            if (steps == 0) return@BlockRenderer

                            val targetIndex = (currentIndex + steps).coerceIn(0, localBlocks.lastIndex)
                            if (targetIndex != currentIndex) {
                                val mutable = localBlocks.toMutableList()
                                val item = mutable.removeAt(currentIndex)
                                mutable.add(targetIndex, item)
                                localBlocks = mutable
                                // Reset accumulated delta (we moved by `steps` rows)
                                accumulatedDragY -= steps * blockRowHeightPx
                            }
                        },
                        onDragEnd = {
                            if (draggingBlockId != null) {
                                // Re-assign sortOrder to match visual position
                                val reordered = localBlocks.mapIndexed { i, b ->
                                    when (b) {
                                        is TextBlock     -> b.copy(sortOrder = i)
                                        is CheckboxBlock -> b.copy(sortOrder = i)
                                        is ImageBlock    -> b.copy(sortOrder = i)
                                        is TableBlock    -> b.copy(sortOrder = i)
                                        is HeadingBlock  -> b.copy(sortOrder = i)
                                        is DividerBlock  -> b.copy(sortOrder = i)
                                        is LinkBlock     -> b.copy(sortOrder = i)
                                    }
                                }
                                localBlocks = reordered
                                onReorderBlocks(reordered)
                            }
                            draggingBlockId = null
                            accumulatedDragY = 0f
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(Dimens.spaceMax))
            }
        }

        // ── Floating Block Insert Toolbar ──────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Dimens.spaceXL)
        ) {
            EditorBottomToolbar(
                currentBlockCount = blocks.size,
                formattingState = formattingState,
                hasActiveBlock = activeBlockId != null,
                onAddBlock = onAddBlock,
                onFormatClick = { marker ->
                    // Capture the selection NOW, before focus moves to the button
                    val sel = activeSelection.selection
                    pendingFormatCommand = FormatCommand(
                        marker = marker,
                        selectionStart = sel.start,
                        selectionEnd = sel.end
                    )
                }
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
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXS),
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
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .border(Dimens.spaceBorder, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), MaterialTheme.shapes.extraSmall)
                    .padding(horizontal = Dimens.spaceXS, vertical = Dimens.spaceXXS)
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
                    .size(Dimens.iconNormal)
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
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .border(Dimens.spaceBorder, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), MaterialTheme.shapes.extraSmall)
            .padding(start = Dimens.spaceXS, top = Dimens.spaceXXS, bottom = Dimens.spaceXXS, end = Dimens.spaceXXS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXXS)
    ) {
        Text(
            text = "#$tag",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(Dimens.iconSmall)
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
// Editor Bottom Toolbar (Formatting & Insert)
// ─────────────────────────────────────────────────────────────────────────────

private data class InsertEntry(
    val label: String,
    val icon: ImageVector,
    val makeBlock: (id: String, sortOrder: Int) -> ContentBlock
)

@Composable
private fun EditorBottomToolbar(
    currentBlockCount: Int,
    formattingState: FormattingState,
    hasActiveBlock: Boolean,
    onAddBlock: (ContentBlock) -> Unit,
    onFormatClick: (String) -> Unit
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
        InsertEntry("Link", Icons.Default.Link)
        { id, order -> LinkBlock(id, order, "", "", "generic") },
    )

    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .border(
                width = Dimens.spaceBorder,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.large
            )
            .padding(horizontal = Dimens.spaceS, vertical = Dimens.spaceXS),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Formatting section (only when a text block is active) ──────────
        AnimatedVisibility(
            visible = hasActiveBlock,
            enter = fadeIn(animationSpec = Motion.Spec.enter()) +
                    androidx.compose.animation.expandHorizontally(animationSpec = Motion.Spec.springStandard()),
            exit  = fadeOut(animationSpec = Motion.Spec.exit()) +
                    androidx.compose.animation.shrinkHorizontally(animationSpec = Motion.Spec.springStiff())
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXXS),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToolbarIconButton(
                    icon = Icons.Default.FormatBold,
                    description = "Bold",
                    isActive = formattingState.isBold,
                    onClick = { onFormatClick("**") }
                )
                ToolbarIconButton(
                    icon = Icons.Default.FormatItalic,
                    description = "Italic",
                    isActive = formattingState.isItalic,
                    onClick = { onFormatClick("*") }
                )
                ToolbarIconButton(
                    icon = Icons.Default.FormatUnderlined,
                    description = "Underline",
                    isActive = formattingState.isUnderline,
                    onClick = { onFormatClick("__") }
                )
                ToolbarIconButton(
                    icon = Icons.Default.FormatStrikethrough,
                    description = "Strikethrough",
                    isActive = formattingState.isStrike,
                    onClick = { onFormatClick("~~") }
                )

                // Divider between format and insert sections
                Box(
                    modifier = Modifier
                        .width(Dimens.spaceBorder)
                        .height(Dimens.spaceXL)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                )
            }
        }

        // ── Insert section ────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXS)) {
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
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector, 
    description: String, 
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(Dimens.iconLarge)
            .clip(MaterialTheme.shapes.small)
            .background(if (isActive) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .focusProperties { canFocus = false }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.iconMedium)
        )
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

    val animatedBackground by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = Motion.Spec.standard()
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1.0f,
        animationSpec = Motion.Spec.springBouncy()
    )

    Column(
        modifier = Modifier
            .scale(animatedScale)
            .clip(MaterialTheme.shapes.medium)
            .background(animatedBackground)
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false }
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spaceS, vertical = Dimens.spaceXS),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceXXS)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isHovered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.iconNormal)
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
