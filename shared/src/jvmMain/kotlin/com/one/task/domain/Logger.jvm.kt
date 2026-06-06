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
            
            val timestamp = SimpleDateFormat("yyyyMMdd").format(Date())
            logFile = File(dir, "app_$timestamp.log")
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
