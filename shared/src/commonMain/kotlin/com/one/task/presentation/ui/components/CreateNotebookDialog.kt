package com.one.task.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CreateNotebookDialog(
    onDismissRequest: () -> Unit,
    onCreate: (name: String, iconName: String, colorHex: String, isPrivate: Boolean) -> Unit
) {
    var notebookName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("FolderSpecial") }
    var selectedColor by remember { mutableStateOf("#B496FF") } // Purple
    var isPrivate by remember { mutableStateOf(false) }
    
    val icons = mapOf(
        "Book" to Icons.Default.Book,
        "Folder" to Icons.Default.Folder,
        "Lightbulb" to Icons.Default.Lightbulb,
        "TrackChanges" to Icons.Default.TrackChanges,
        "Code" to Icons.Default.Code,
        "Palette" to Icons.Default.Palette
    )
    
    val colors = listOf(
        "#B496FF", // Purple
        "#A8C8FF", // Blue
        "#FFB869", // Orange
        "#FFB4AB", // Pink
        "#958EA0"  // Grey
    )

    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onDismissRequest),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(480.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2B2D31)) // Match screenshot bg
                    .clickable(enabled = false) {}
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF313338))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create New Notebook", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
                    }
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp).clickable(onClick = onDismissRequest)
                    )
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    // Notebook Name
                    Text("NOTEBOOK NAME", color = Color.Gray, style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E1F22))
                            .padding(12.dp)
                    ) {
                        if (notebookName.isEmpty()) {
                            Text("e.g. Q3 Marketing Plan", color = Color.DarkGray, style = MaterialTheme.typography.bodyLarge)
                        }
                        BasicTextField(
                            value = notebookName,
                            onValueChange = { notebookName = it },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                            cursorBrush = SolidColor(Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Select Icon
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("SELECT ICON", color = Color.Gray, style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
                        Text("Browse all", color = Color(0xFFB496FF), style = MaterialTheme.typography.labelMedium, modifier = Modifier.clickable { })
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        icons.forEach { (name, icon) ->
                            val isSelected = selectedIcon == name
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFFB496FF).copy(alpha = 0.2f) else Color(0xFF1E1F22))
                                    .border(if (isSelected) 2.dp else 0.dp, if (isSelected) Color(0xFFB496FF) else Color.Transparent, CircleShape)
                                    .clickable { selectedIcon = name },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = name, tint = if (isSelected) Color(0xFFB496FF) else Color.LightGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Accent Color
                    Text("ACCENT COLOR", color = Color.Gray, style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        colors.forEach { hex ->
                            val color = Color(hex.removePrefix("#").toLong(16) or 0xFF000000)
                            val isSelected = selectedColor == hex
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(if (isSelected) 2.dp else 0.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
                                    .clickable { selectedColor = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        // Custom color add button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E1F22))
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Private Workspace toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E1F22))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Private Workspace", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
                            Text("Only you can access this notebook", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                        }
                        Switch(
                            checked = isPrivate,
                            onCheckedChange = { isPrivate = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFB496FF)
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Buttons
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel", color = Color.LightGray)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { onCreate(notebookName, selectedIcon, selectedColor, isPrivate) },
                            enabled = notebookName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB496FF), disabledContainerColor = Color(0xFFB496FF).copy(alpha = 0.5f))
                        ) {
                            Text("Create Notebook", color = Color(0xFF340080), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
