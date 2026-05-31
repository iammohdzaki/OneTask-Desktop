package com.one.task.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

import com.one.task.domain.Notebook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.graphics.Color

@Composable
fun RailSidebar(
    notebooks: List<Notebook>,
    activeNotebookId: String?,
    onCreateNotebookClick: () -> Unit,
    onSelectNotebook: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .fillMaxHeight()
            .background(Color(0xFF1E1F22)) // Discord's very dark gray
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add Notebook Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF313338))
                .clickable { onCreateNotebookClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Notebook", tint = Color(0xFF23A559)) // Green Add
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.width(32.dp).height(2.dp).background(Color(0xFF313338)))
        Spacer(modifier = Modifier.height(16.dp))
        
        // Dynamic Notebooks
        notebooks.forEach { notebook ->
            val isSelected = notebook.id == activeNotebookId
            val iconColor = if (notebook.colorHex != null) Color(notebook.colorHex.removePrefix("#").toLong(16) or 0xFF000000) else MaterialTheme.colorScheme.primary
            val iconVector = when (notebook.iconName) {
                "Book" -> Icons.Default.Book
                "Folder" -> Icons.Default.Folder
                "Lightbulb" -> Icons.Default.Lightbulb
                "TrackChanges" -> Icons.Default.TrackChanges
                "Code" -> Icons.Default.Code
                "Palette" -> Icons.Default.Palette
                "FolderSpecial" -> Icons.Default.FolderSpecial
                else -> Icons.Default.FolderSpecial
            }
            
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(48.dp)
                    .clip(if (isSelected) RoundedCornerShape(16.dp) else CircleShape)
                    .background(if (isSelected) iconColor.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onSelectNotebook(notebook.id) },
                contentAlignment = Alignment.Center
            ) {
                Icon(iconVector, contentDescription = notebook.name, tint = if (isSelected) iconColor else MaterialTheme.colorScheme.onSurfaceVariant)
                
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .width(4.dp)
                            .fillMaxHeight(0.6f)
                            .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                            .background(iconColor)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        RailIcon(Icons.Outlined.Settings, stringResource(Res.string.content_desc_settings))
    }
}

@Composable
fun RailIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String) {
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .size(48.dp)
            .clip(CircleShape)
            .hoverableBackground(hoverColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = description, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
