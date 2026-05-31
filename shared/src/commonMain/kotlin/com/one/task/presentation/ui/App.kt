package com.one.task.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.one.task.presentation.ui.screens.WorkspaceScreen

@Composable
fun App(titleBar: @Composable () -> Unit = {}) {
    OneTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                titleBar() // The custom window title bar
                WorkspaceScreen()
            }
        }
    }
}
