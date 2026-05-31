package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.task.domain.TableBlock
import com.one.task.presentation.ui.components.hoverableBackground
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun TableBlockEditor(block: TableBlock, onUpdate: (TableBlock) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(block.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        }
        
        // Headers
        Row(modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(stringResource(Res.string.table_header_name), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(2f))
            Text(stringResource(Res.string.table_header_role), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            Text(stringResource(Res.string.table_header_status), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        }
        
        // Sample Rows
        TableRow("J. Doe", "JD", MaterialTheme.colorScheme.primary, "Lead Eng", MaterialTheme.colorScheme.primary)
        TableRow("S. Wong", "SW", MaterialTheme.colorScheme.tertiary, "DevOps", MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
private fun TableRow(name: String, initials: String, color: Color, role: String, statusColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .hoverableBackground(hoverColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(2f)) {
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp), color = color)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, style = MaterialTheme.typography.bodyLarge)
        }
        Text(role, style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant), modifier = Modifier.weight(1f))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor))
        }
    }
}
