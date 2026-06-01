package com.one.task.domain

import java.util.UUID
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap

actual fun pickFile(title: String, allowedExtensions: List<String>): String? {
    val dialog = FileDialog(null as Frame?, title, FileDialog.LOAD)
    
    // On Windows, multiple extensions are separated by semicolon
    val filter = allowedExtensions.joinToString(";") { "*.$it" }
    dialog.file = filter
    
    dialog.isVisible = true
    
    val file = dialog.file
    val dir = dialog.directory
    return if (file != null && dir != null) File(dir, file).absolutePath else null
}

actual fun generateUuid(): String = UUID.randomUUID().toString()
actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun loadLocalImage(path: String): ImageBitmap? {
    return try {
        val file = File(path)
        if (file.exists()) {
            file.inputStream().buffered().use { loadImageBitmap(it) }
        } else null
    } catch (e: Exception) {
        null
    }
}
