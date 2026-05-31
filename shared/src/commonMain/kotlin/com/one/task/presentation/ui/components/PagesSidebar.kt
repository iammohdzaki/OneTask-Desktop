package com.one.task.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.task.domain.Page
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LibraryBooks

@Composable
fun PagesSidebar(
    pages: List<Page>,
    selectedPageId: String?,
    onSelect: (Page) -> Unit,
    onCreatePage: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(stringResource(Res.string.sidebar_pages), style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp))
            Text(stringResource(Res.string.last_edited), style = MaterialTheme.typography.labelMedium)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (pages.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onCreatePage, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Page")
                    }
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(pages) { page ->
                    val isSelected = page.id == selectedPageId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent)
                            .clickable { onSelect(page) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Box(modifier = Modifier.width(4.dp).height(24.dp).background(MaterialTheme.colorScheme.primary).offset(x = (-16).dp))
                        }
                        val icon = if (isSelected) Icons.Filled.RocketLaunch else Icons.Outlined.Description
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp).padding(end = 12.dp)
                        )
                        Text(
                            text = page.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Footer actions
        Column(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp).hoverableBackground(hoverColor = MaterialTheme.colorScheme.surfaceContainerHigh).clip(RoundedCornerShape(8.dp))) {
                Icon(Icons.Outlined.Archive, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end=12.dp))
                Text(stringResource(Res.string.sidebar_archive), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp).hoverableBackground(hoverColor = MaterialTheme.colorScheme.surfaceContainerHigh).clip(RoundedCornerShape(8.dp))) {
                Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end=12.dp))
                Text(stringResource(Res.string.sidebar_trash), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
