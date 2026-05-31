package com.one.task.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.one.task.data.db.AppDatabase
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val appDataPath = System.getenv("APPDATA") ?: System.getProperty("user.home")
        val dbDir = File(appDataPath, "OneTask")
        if (!dbDir.exists()) {
            dbDir.mkdirs()
        }
        val dbFile = File(dbDir, "onetask.db")
        if (dbFile.exists()) {
            dbFile.delete() // Reset schema for prototype
        }
        val dbExists = false
        
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        
        // Ensure schema is created only once
        if (!dbExists) {
            AppDatabase.Schema.create(driver)
        }
        
        return driver
    }
}
