package com.one.task.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual fun createDataStore(): DataStore<Preferences> {
    val dataStoreDir = getAppDataDir()
    if (!dataStoreDir.exists()) {
        dataStoreDir.mkdirs()
    }
    val dataStoreFile = File(dataStoreDir, "settings.preferences_pb")
    return createDataStore(
        producePath = { dataStoreFile.absolutePath }
    )
}
