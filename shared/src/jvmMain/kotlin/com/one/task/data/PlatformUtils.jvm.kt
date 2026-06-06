package com.one.task.data

import java.io.File

fun getAppDataDir(): File {
    val isDebug = System.getProperty("onetask.debug") == "true"
    
    return if (isDebug) {
        // Use a local folder in the project root during development
        File("dev-data")
    } else {
        // Standard AppData folder for production
        val appDataPath = System.getenv("APPDATA") ?: System.getProperty("user.home")
        File(appDataPath, "OneTask")
    }
}
