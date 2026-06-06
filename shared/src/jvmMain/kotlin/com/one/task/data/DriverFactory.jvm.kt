package com.one.task.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.one.task.data.db.AppDatabase
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val customPath = BootConfig.getDatabasePath()
        val dbDir = if (customPath != null) {
            File(customPath)
        } else {
            getAppDataDir()
        }
        
        if (!dbDir.exists()) {
            dbDir.mkdirs()
        }
        val dbFile = File(dbDir, "onetask.db")
        
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        
        // Handle migrations safely
        var currentVersion = driver.executeQuery(null, "PRAGMA user_version;", { cursor ->
            QueryResult.Value(if (cursor.next().value) cursor.getLong(0) ?: 0L else 0L)
        }, 0).value
        
        val latestVersion = AppDatabase.Schema.version
        
        if (currentVersion == 0L) {
            // Check if tables already exist (from a previous version that didn't use user_version)
            val tablesExist = driver.executeQuery(null, "SELECT name FROM sqlite_master WHERE type='table' AND name='NotebookEntity';", { cursor ->
                QueryResult.Value(cursor.next().value)
            }, 0).value
            
            if (tablesExist) {
                // If tables exist but version is 0, set it to the latest version as we assume it's up to date
                // or at least created before version tracking was added.
                driver.execute(null, "PRAGMA user_version = $latestVersion;", 0)
                currentVersion = latestVersion
            } else {
                AppDatabase.Schema.create(driver)
                driver.execute(null, "PRAGMA user_version = $latestVersion;", 0)
                currentVersion = latestVersion
            }
        }
        
        if (currentVersion < latestVersion) {
            AppDatabase.Schema.migrate(driver, currentVersion, latestVersion)
            driver.execute(null, "PRAGMA user_version = $latestVersion;", 0)
        }
        
        return driver
    }
}
