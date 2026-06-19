package com.one.task.data

import java.io.File

/**
 * Returns true when running in a development environment (Gradle run / IDE).
 *
 * A packaged Compose Desktop distribution never ships with `gradlew` or
 * `build.gradle.kts` next to the executable, so their presence in the
 * current working directory is a reliable, zero-config signal that we are
 * in a dev build. No Gradle task wiring or JVM system properties needed.
 */
private fun isDebugEnvironment(): Boolean =
    File("gradlew").exists() || File("build.gradle.kts").exists()

fun getAppDataDir(): File {
    return if (isDebugEnvironment()) {
        // Use a local folder in the project root during development
        File("dev-data")
    } else {
        // Standard AppData/OneTask folder for production
        val appDataPath = System.getenv("APPDATA") ?: System.getProperty("user.home")
        File(appDataPath, "OneTask")
    }
}
