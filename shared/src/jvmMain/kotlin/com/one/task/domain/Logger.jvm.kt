package com.one.task.domain

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*
import com.one.task.data.getAppDataDir

actual object Logger {
    private val logger = LoggerFactory.getLogger("OneTask")
    private var logFile: File? = null

    init {
        try {
            val dir = File(getAppDataDir(), "logs")
            if (!dir.exists()) dir.mkdirs()
            
            logFile = File(dir, "app.log")
            if (logFile?.exists() == true) {
                logFile?.delete()
            }
            logFile?.createNewFile()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Performs heavy IO operations like clearing old logs.
     * Should be called from a background thread during app startup.
     */
    fun initializeAsync() {
        try {
            val dir = File(getAppDataDir(), "logs")
            if (dir.exists()) {
                // Clear old date-based logs if they exist
                dir.listFiles()?.filter { it.name.startsWith("app_") && it.name.endsWith(".log") }?.forEach { it.delete() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun writeToFile(level: String, tag: String, message: String, throwable: Throwable? = null) {
        try {
            logFile?.let { file ->
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
                val logLine = "$timestamp [$level] [$tag] $message"
                
                FileOutputStream(file, true).use { fos ->
                    val writer = PrintWriter(fos)
                    writer.println(logLine)
                    throwable?.printStackTrace(writer)
                    writer.flush()
                }
            }
        } catch (e: Exception) {
            // Fallback to console if file writing fails
            e.printStackTrace()
        }
    }

    actual fun d(tag: String, message: String) {
        logger.info("[$tag] [DEBUG] $message")
        writeToFile("DEBUG", tag, message)
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            logger.error("[$tag] $message", throwable)
        } else {
            logger.error("[$tag] $message")
        }
        writeToFile("ERROR", tag, message, throwable)
    }

    actual fun i(tag: String, message: String) {
        logger.info("[$tag] $message")
        writeToFile("INFO", tag, message)
    }
}
