package com.one.task.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository {
    val themeMode: Flow<String>
    val passwordAuthEnabled: Flow<Boolean>
    val fontSize: Flow<Int>
    val fullWidthEditor: Flow<Boolean>
    val showLineNumbers: Flow<Boolean>
    val autoSave: Flow<Boolean>

    suspend fun setThemeMode(mode: String)
    suspend fun setPasswordAuthEnabled(enabled: Boolean)
    suspend fun setFontSize(size: Int)
    suspend fun setFullWidthEditor(enabled: Boolean)
    suspend fun setShowLineNumbers(enabled: Boolean)
    suspend fun setAutoSave(enabled: Boolean)
}

class DefaultSettingsRepository(private val dataStore: DataStore<Preferences>) : SettingsRepository {

    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PASSWORD_AUTH = booleanPreferencesKey("password_auth")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val FULL_WIDTH_EDITOR = booleanPreferencesKey("full_width_editor")
        val SHOW_LINE_NUMBERS = booleanPreferencesKey("show_line_numbers")
        val AUTO_SAVE = booleanPreferencesKey("auto_save")
    }

    override val themeMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: "System"
    }

    override val passwordAuthEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PASSWORD_AUTH] ?: false
    }

    override val fontSize: Flow<Int> = dataStore.data.map { prefs ->
        prefs[FONT_SIZE]?.toIntOrNull() ?: 16
    }

    override val fullWidthEditor: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[FULL_WIDTH_EDITOR] ?: false
    }

    override val showLineNumbers: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SHOW_LINE_NUMBERS] ?: false
    }

    override val autoSave: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[AUTO_SAVE] ?: true
    }

    override suspend fun setThemeMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[THEME_MODE] = mode
        }
    }

    override suspend fun setPasswordAuthEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PASSWORD_AUTH] = enabled
        }
    }

    override suspend fun setFontSize(size: Int) {
        dataStore.edit { prefs ->
            prefs[FONT_SIZE] = size.toString()
        }
    }

    override suspend fun setFullWidthEditor(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[FULL_WIDTH_EDITOR] = enabled
        }
    }

    override suspend fun setShowLineNumbers(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[SHOW_LINE_NUMBERS] = enabled
        }
    }

    override suspend fun setAutoSave(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[AUTO_SAVE] = enabled
        }
    }
}