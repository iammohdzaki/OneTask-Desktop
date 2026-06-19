package com.one.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import androidx.compose.foundation.isSystemInDarkTheme
import com.one.task.data.DriverFactory
import com.one.task.di.initKoin
import com.one.task.domain.Logger
import com.one.task.presentation.ui.App
import com.one.task.presentation.ui.OneTaskTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

        val state = rememberWindowState(
            width = 1200.dp,
            height = 800.dp,
            position = WindowPosition(Alignment.Center)
        )

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
                    kotlinx.coroutines.delay(1800.milliseconds)
                    isReady = true
                }
            }

            OneTaskTheme(darkTheme = isSystemInDarkTheme()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    CustomTitleBar(
                        onClose = ::exitApplication,
                        onMinimize = { state.isMinimized = true },
                        onMaximize = {
                            state.placement =
                                if (state.placement == WindowPlacement.Maximized)
                                    WindowPlacement.Floating
                                else
                                    WindowPlacement.Maximized
                        }
                    )

                    if (!isReady) {
                        SplashScreen(modifier = Modifier.weight(1f).fillMaxWidth())
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
