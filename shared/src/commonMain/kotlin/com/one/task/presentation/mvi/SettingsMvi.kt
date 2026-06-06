package com.one.task.presentation.mvi

data class SettingsState(
    val themeMode: String = "System",
    val passwordAuthEnabled: Boolean = false,
    val fontSize: Int = 16,
    val fullWidthEditor: Boolean = false,
    val showLineNumbers: Boolean = false,
    val autoSave: Boolean = true,
    val databasePath: String? = null
)

sealed interface SettingsIntent {
    data class SetThemeMode(val mode: String) : SettingsIntent
    data class SetPasswordAuthEnabled(val enabled: Boolean) : SettingsIntent
    data class SetFontSize(val size: Int) : SettingsIntent
    data class SetFullWidthEditor(val enabled: Boolean) : SettingsIntent
    data class SetShowLineNumbers(val enabled: Boolean) : SettingsIntent
    data class SetAutoSave(val enabled: Boolean) : SettingsIntent
    data object ClearAllData : SettingsIntent
    data class SetDatabasePath(val path: String?) : SettingsIntent
    data class ExportData(val onComplete: (String) -> Unit) : SettingsIntent
    data class ImportData(val json: String, val onComplete: () -> Unit) : SettingsIntent
}
