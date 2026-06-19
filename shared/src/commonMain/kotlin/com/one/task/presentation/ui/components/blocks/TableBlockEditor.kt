package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.task.domain.TableBlock
import com.one.task.presentation.ui.utils.add_column_right
import com.one.task.presentation.ui.utils.add_row_below
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TableBlockEditor(block: TableBlock, onUpdate: (TableBlock) -> Unit) {
    var localTitle by remember(block.id) { mutableStateOf(block.title) }
    var isEditing by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, if (isEditing) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false }
    ) {
        // Table Title/Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isEditing) {
                BasicTextField(
                    value = localTitle,
                    onValueChange = {
                        localTitle = it
                        onUpdate(block.copy(title = it))
                    },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (localTitle.isEmpty()) {
                            Text(
                                "Table Title",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        innerTextField()
                    }
                )
            } else {
                Text(
                    text = localTitle.ifEmpty { "Table Title" },
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (localTitle.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Row/Col Management
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isEditing) {
                    TableActionButton(add_row_below, stringResource(Res.string.content_desc_add_row_below)) {
                        val newData = block.data.toMutableList()
                        newData.add(List(block.cols) { "" })
                        onUpdate(block.copy(rows = block.rows + 1, data = newData))
                    }
                    TableActionButton(Icons.Default.Close, stringResource(Res.string.content_desc_remove_last_row)) {
                        if (block.rows > 1) {
                            val newData = block.data.dropLast(1)
                            onUpdate(block.copy(rows = block.rows - 1, data = newData))
                        }
                    }
                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(MaterialTheme.colorScheme.outlineVariant))
                    TableActionButton(add_column_right, stringResource(Res.string.content_desc_add_column_right)) {
                        val newData = block.data.map { it.toMutableList().apply { add("") } }
                        onUpdate(block.copy(cols = block.cols + 1, data = newData))
                    }
                    TableActionButton(Icons.Default.Close, stringResource(Res.string.content_desc_remove_last_col)) {
                        if (block.cols > 1) {
                            val newData = block.data.map { it.dropLast(1) }
                            onUpdate(block.copy(cols = block.cols - 1, data = newData))
                        }
                    }
                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(MaterialTheme.colorScheme.outlineVariant))
                    TableActionButton(Icons.Default.Check, stringResource(Res.string.content_desc_done_editing), MaterialTheme.colorScheme.primary) {
                        isEditing = false
                    }
                } else if (isHovered) {
                    TableActionButton(Icons.Default.Edit, stringResource(Res.string.content_desc_edit_table)) {
                        isEditing = true
                    }
                }
            }
        }

        // Table Content
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            block.data.forEachIndexed { rowIndex, rowData ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)   // all cells in a row match the tallest cell
                ) {
                    rowData.forEachIndexed { colIndex, cellText ->
                        TableCell(
                            text = cellText,
                            isFirstRow = rowIndex == 0,
                            isFirstCol = colIndex == 0,
                            isEditing = isEditing,
                            onValueChange = { newValue ->
                                val newData = block.data.mapIndexed { r, rData ->
                                    if (r == rowIndex) {
                                        rData.mapIndexed { c, cText ->
                                            if (c == colIndex) newValue else cText
                                        }
                                    } else rData
                                }
                                onUpdate(block.copy(data = newData))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.TableCell(
    text: String,
    isFirstRow: Boolean,
    isFirstCol: Boolean,
    isEditing: Boolean,
    onValueChange: (String) -> Unit
) {
    var localText by remember(text == "") { mutableStateOf(text) }
    
    LaunchedEffect(text) {
        if (text != localText) localText = text
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()   // stretch to the row height set by IntrinsicSize.Min
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            .background(if (isFirstRow) MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.3f) else Color.Transparent)
            .padding(8.dp)
    ) {
        if (isEditing) {
            BasicTextField(
                value = localText,
                onValueChange = {
                    localText = it
                    onValueChange(it)
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isFirstRow) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isFirstRow) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = localText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isFirstRow) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isFirstRow) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TableActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    description: String, 
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(28.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}
