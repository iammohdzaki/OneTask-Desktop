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
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetThemeMode -> {
                viewModelScope.launch { repository.setThemeMode(intent.mode) }
            }
            is SettingsIntent.SetPasswordAuthEnabled -> {
                viewModelScope.launch { repository.setPasswordAuthEnabled(intent.enabled) }
            }
        }
    }
}