package com.one.task.presentation.ui.utils

interface FilePicker {
    fun saveFile(fileName: String, content: String, extension: String)
    fun pickFile(extension: String, onFileRead: (String) -> Unit)
    fun pickFolder(onFolderSelected: (String) -> Unit)
}

expect fun getFilePicker(): FilePicker
