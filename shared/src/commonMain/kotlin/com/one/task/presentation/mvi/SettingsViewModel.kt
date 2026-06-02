package com.one.task.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.one.task.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

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
    }

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
        }
    }
}
