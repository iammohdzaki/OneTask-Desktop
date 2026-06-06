package com.one.task.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.one.task.data.SettingsRepository
import com.one.task.data.BootConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val taskRepository: com.one.task.data.TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.themeMode.collect { mode ->
                _state.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            repository.passwordAuthEnabled.collect { enabled ->
                _state.update { it.copy(passwordAuthEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            repository.fontSize.collect { size ->
                _state.update { it.copy(fontSize = size) }
            }
        }
        viewModelScope.launch {
            repository.fullWidthEditor.collect { enabled ->
                _state.update { it.copy(fullWidthEditor = enabled) }
            }
        }
        viewModelScope.launch {
            repository.showLineNumbers.collect { enabled ->
                _state.update { it.copy(showLineNumbers = enabled) }
            }
        }
        viewModelScope.launch {
            repository.autoSave.collect { enabled ->
                _state.update { it.copy(autoSave = enabled) }
            }
        }
        viewModelScope.launch {
            repository.databasePath.collect { path ->
                _state.update { it.copy(databasePath = path) }
            }
        }
    }

    private val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetThemeMode -> {
                viewModelScope.launch { repository.setThemeMode(intent.mode) }
            }
            is SettingsIntent.SetPasswordAuthEnabled -> {
                viewModelScope.launch { repository.setPasswordAuthEnabled(intent.enabled) }
            }
            is SettingsIntent.SetFontSize -> {
                viewModelScope.launch { repository.setFontSize(intent.size) }
            }
            is SettingsIntent.SetFullWidthEditor -> {
                viewModelScope.launch { repository.setFullWidthEditor(intent.enabled) }
            }
            is SettingsIntent.SetShowLineNumbers -> {
                viewModelScope.launch { repository.setShowLineNumbers(intent.enabled) }
            }
            is SettingsIntent.SetAutoSave -> {
                viewModelScope.launch { repository.setAutoSave(intent.enabled) }
            }
            is SettingsIntent.SetDatabasePath -> {
                viewModelScope.launch { 
                    repository.setDatabasePath(intent.path)
                    BootConfig.setDatabasePath(intent.path)
                }
            }
            is SettingsIntent.ClearAllData -> {
                viewModelScope.launch {
                    taskRepository.clearAllData()
                    repository.setHasSeededInitialData(false)
                }
            }
            is SettingsIntent.ExportData -> {
                viewModelScope.launch {
                    val backup = taskRepository.getFullWorkspaceBackup()
                    val jsonString = json.encodeToString(com.one.task.domain.WorkspaceBackup.serializer(), backup)
                    intent.onComplete(jsonString)
                }
            }
            is SettingsIntent.ImportData -> {
                viewModelScope.launch {
                    try {
                        val backup = json.decodeFromString(com.one.task.domain.WorkspaceBackup.serializer(), intent.json)
                        taskRepository.restoreFromBackup(backup)
                        repository.setHasSeededInitialData(true)
                        intent.onComplete()
                    } catch (e: Exception) {
                        com.one.task.domain.Logger.e("SettingsViewModel", "Failed to import data", e)
                    }
                }
            }
        }
    }
}
