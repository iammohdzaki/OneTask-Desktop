package com.one.task.domain

import androidx.compose.ui.graphics.ImageBitmap

expect fun generateUuid(): String
expect fun currentTimeMillis(): Long
expect fun pickFile(title: String, allowedExtensions: List<String>): String?

fun pickImageFile(): String? {
    return pickFile("Select Image", listOf("png", "jpg", "jpeg", "svg"))
}
expect fun loadLocalImage(path: String): ImageBitmap?
expect suspend fun loadNetworkImage(url: String): ImageBitmap?
