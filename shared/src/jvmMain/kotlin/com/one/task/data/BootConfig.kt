package com.one.task.data

import java.io.File
import java.util.Properties

actual object BootConfig {
    private const val CONFIG_FILE = "boot.properties"
    private const val KEY_DB_PATH = "database_path"

    private fun getConfigFile(): File {
        val dir = getAppDataDir()
        if (!dir.exists()) dir.mkdirs()
        return File(dir, CONFIG_FILE)
    }

    actual fun getDatabasePath(): String? {
        val file = getConfigFile()
        if (!file.exists()) return null
        val props = Properties()
        file.inputStream().use { props.load(it) }
        return props.getProperty(KEY_DB_PATH)
    }

    actual fun setDatabasePath(path: String?) {
        val file = getConfigFile()
        val props = Properties()
        if (file.exists()) {
            file.inputStream().use { props.load(it) }
        }
        if (path == null) {
            props.remove(KEY_DB_PATH)
        } else {
            props.setProperty(KEY_DB_PATH, path)
        }
        file.outputStream().use { props.store(it, "OneTask Boot Configuration") }
    }
}
