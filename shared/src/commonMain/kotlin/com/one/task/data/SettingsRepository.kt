package com.one.task.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PASSWORD_AUTH = booleanPreferencesKey("password_auth")
    }

    val themeMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: "System"
    }

    val passwordAuthEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PASSWORD_AUTH] ?: false
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[THEME_MODE] = mode
        }
    }

    suspend fun setPasswordAuthEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PASSWORD_AUTH] = enabled
        }
    }
}