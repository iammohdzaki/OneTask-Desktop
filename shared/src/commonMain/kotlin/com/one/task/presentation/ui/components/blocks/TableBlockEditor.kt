package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.task.domain.TableBlock

@Composable
fun TableBlockEditor(block: TableBlock, onUpdate: (TableBlock) -> Unit) {
    var localTitle by remember(block.id) { mutableStateOf(block.title) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
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

            // Row/Col Management
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TableActionButton(Icons.Default.Add, "Add Row") {
                    val newData = block.data.toMutableList()
                    newData.add(List(block.cols) { "" })
                    onUpdate(block.copy(rows = block.rows + 1, data = newData))
                }
                TableActionButton(Icons.Default.Remove, "Remove Row") {
                    if (block.rows > 1) {
                        val newData = block.data.dropLast(1)
                        onUpdate(block.copy(rows = block.rows - 1, data = newData))
                    }
                }
                Box(modifier = Modifier.width(1.dp).height(20.dp).background(MaterialTheme.colorScheme.outlineVariant))
                TableActionButton(Icons.Default.Add, "Add Col") {
                    val newData = block.data.map { it.toMutableList().apply { add("") } }
                    onUpdate(block.copy(cols = block.cols + 1, data = newData))
                }
                TableActionButton(Icons.Default.Remove, "Remove Col") {
                    if (block.cols > 1) {
                        val newData = block.data.map { it.dropLast(1) }
                        onUpdate(block.copy(cols = block.cols - 1, data = newData))
                    }
                }
            }
        }

        // Table Content
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            block.data.forEachIndexed { rowIndex, rowData ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowData.forEachIndexed { colIndex, cellText ->
                        TableCell(
                            text = cellText,
                            isFirstRow = rowIndex == 0,
                            isFirstCol = colIndex == 0,
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
    onValueChange: (String) -> Unit
) {
    var localText by remember(text == "") { mutableStateOf(text) }
    
    LaunchedEffect(text) {
        if (text != localText) localText = text
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            .background(if (isFirstRow) MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.3f) else Color.Transparent)
            .padding(8.dp)
    ) {
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
    }
}

@Composable
private fun TableActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(28.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}
