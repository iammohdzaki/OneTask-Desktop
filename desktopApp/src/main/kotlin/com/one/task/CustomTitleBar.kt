package com.one.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import com.one.task.presentation.ui.components.hoverableBackground
import onetask.shared.generated.resources.Res
import onetask.shared.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun WindowScope.CustomTitleBar(
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit
) {
    WindowDraggableArea {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(Res.string.app_name),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 16.dp).weight(1f)
            )

            Row {
                TitleBarIconButton(Icons.Default.Minimize, onClick = onMinimize)
                TitleBarIconButton(Icons.Default.CropSquare, onClick = onMaximize)
                TitleBarIconButton(
                    Icons.Default.Close,
                    onClick = onClose,
                    hoverColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TitleBarIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    hoverColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    Box(
        modifier = Modifier
            .width(46.dp)
            .fillMaxHeight()
            .hoverableBackground(hoverColor = hoverColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}
