package com.one.task.presentation.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.one.task.data.SettingsRepository
import com.one.task.presentation.ui.screens.WorkspaceScreen
import org.koin.compose.koinInject

@Composable
fun App(titleBar: @Composable () -> Unit = {}) {
    val settingsRepository: SettingsRepository = koinInject()
    val themeMode by settingsRepository.themeMode.collectAsState(initial = "System")
    val isSystemDark = isSystemInDarkTheme()
    
    val darkTheme = when (themeMode) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemDark
    }

    OneTaskTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                titleBar() // The custom window title bar
                WorkspaceScreen()
            }
        }
    }
}
