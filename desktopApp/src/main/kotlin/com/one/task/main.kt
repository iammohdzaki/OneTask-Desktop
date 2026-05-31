package com.one.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import androidx.compose.foundation.window.WindowDraggableArea
import com.one.task.data.DriverFactory
import com.one.task.di.initKoin
import com.one.task.presentation.ui.App
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Minimize
import com.one.task.presentation.ui.components.hoverableBackground

fun main() = application {
    initKoin(DriverFactory())
    
    val state = rememberWindowState(width = 1000.dp, height = 800.dp)
    
    Window(
        onCloseRequest = ::exitApplication,
        state = state,
        title = "OneTask",
        undecorated = true,
        transparent = false
    ) {
        App(
            titleBar = {
                CustomTitleBar(
                    onClose = ::exitApplication,
                    onMinimize = { state.isMinimized = true },
                    onMaximize = { 
                        state.placement = if (state.placement == WindowPlacement.Maximized) WindowPlacement.Floating else WindowPlacement.Maximized 
                    }
                )
            }
        )
    }
}

@Composable
private fun WindowScope.CustomTitleBar(
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit
) {
    WindowDraggableArea {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest), // Blends with the sidebar
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "OneTask",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 16.dp).weight(1f)
            )
            
            // Window controls
            Row {
                TitleBarIconButton(Icons.Default.Minimize, onClick = onMinimize)
                TitleBarIconButton(Icons.Default.CropSquare, onClick = onMaximize)
                TitleBarIconButton(Icons.Default.Close, onClick = onClose, hoverColor = Color(0xFFED4245)) // Keep red for close
            }
        }
    }
}

@Composable
private fun TitleBarIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}