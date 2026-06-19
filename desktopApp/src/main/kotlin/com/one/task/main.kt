package com.one.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material3.CircularProgressIndicator
import com.one.task.presentation.ui.OneTaskTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.one.task.data.DriverFactory
import com.one.task.di.initKoin
import com.one.task.presentation.ui.App
import com.one.task.presentation.ui.components.hoverableBackground
import com.one.task.domain.Logger
import onetask.shared.generated.resources.Res
import onetask.shared.generated.resources.app_name
import onetask.shared.generated.resources.launcher
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

fun main() {
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        Logger.e("Main", "Unhandled exception in thread ${thread.name}", throwable)
    }

    application {
        Logger.i("Main", "Starting application...")

        val state = rememberWindowState(width = 1200.dp, height = 800.dp, position = WindowPosition(Alignment.Center))

        Window(
            onCloseRequest = ::exitApplication,
            state = state,
            title = stringResource(Res.string.app_name),
            icon = painterResource(Res.drawable.launcher),
            undecorated = true,
            transparent = false
        ) {
            var isReady by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    Logger.initializeAsync()
                    initKoin(DriverFactory())
                    
                    // Add an artificial delay so the beautiful splash screen is visible for a moment
                    kotlinx.coroutines.delay(1500.milliseconds)
                    
                    isReady = true
                }
            }

            OneTaskTheme(darkTheme = isSystemInDarkTheme()) {
                Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    CustomTitleBar(
                        onClose = ::exitApplication,
                        onMinimize = { state.isMinimized = true },
                        onMaximize = {
                            state.placement =
                                if (state.placement == WindowPlacement.Maximized) WindowPlacement.Floating else WindowPlacement.Maximized
                        }
                    )
                    
                    if (!isReady) {
                        AnimatedSplashScreen(modifier = Modifier.weight(1f).fillMaxWidth())
                    } else {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            App(titleBar = {})
                        }
                    }
                }
            }
        }
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
                stringResource(Res.string.app_name),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 16.dp).weight(1f)
            )

            // Window controls
            Row {
                TitleBarIconButton(Icons.Default.Minimize, onClick = onMinimize)
                TitleBarIconButton(Icons.Default.CropSquare, onClick = onMaximize)
                TitleBarIconButton(
                    Icons.Default.Close,
                    onClick = onClose,
                    hoverColor = MaterialTheme.colorScheme.error
                ) // Use theme error color
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

@Composable
fun AnimatedSplashScreen(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Animate scale for breathing effect
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animate alpha for fade effect
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Glow behind the logo
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(scale)
                        .alpha(alpha * 0.3f)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
                androidx.compose.foundation.Image(
                    painter = painterResource(Res.drawable.launcher),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Loading your workspace...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
            )
        }
    }
}
