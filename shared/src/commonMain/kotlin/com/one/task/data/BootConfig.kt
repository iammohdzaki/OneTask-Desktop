package com.one.task.data

expect object BootConfig {
    fun setDatabasePath(path: String?)
    fun getDatabasePath(): String?
}
