package com.one.task.presentation.mvi

data class SettingsState(
    val themeMode: String = "System",
    val passwordAuthEnabled: Boolean = false
)

sealed interface SettingsIntent {
    data class SetThemeMode(val mode: String) : SettingsIntent
    data class SetPasswordAuthEnabled(val enabled: Boolean) : SettingsIntent
}