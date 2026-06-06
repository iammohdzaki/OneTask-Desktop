package com.one.task.presentation.ui.utils

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class JvmFilePicker : FilePicker {
    override fun saveFile(fileName: String, content: String, extension: String) {
        val dialog = FileDialog(null as Frame?, "Save Backup", FileDialog.SAVE)
        dialog.file = "$fileName.$extension"
        dialog.isVisible = true
        val dir = dialog.directory
        val file = dialog.file
        if (dir != null && file != null) {
            File(dir, file).writeText(content)
        }
    }

    override fun pickFile(extension: String, onFileRead: (String) -> Unit) {
        val dialog = FileDialog(null as Frame?, "Select Backup", FileDialog.LOAD)
        dialog.setFilenameFilter { _, name -> name.endsWith(".$extension") }
        dialog.isVisible = true
        val dir = dialog.directory
        val file = dialog.file
        if (dir != null && file != null) {
            val content = File(dir, file).readText()
            onFileRead(content)
        }
    }

    override fun pickFolder(onFolderSelected: (String) -> Unit) {
        val chooser = javax.swing.JFileChooser()
        chooser.fileSelectionMode = javax.swing.JFileChooser.DIRECTORIES_ONLY
        chooser.dialogTitle = "Select Database Folder"
        val result = chooser.showOpenDialog(null)
        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            onFolderSelected(chooser.selectedFile.absolutePath)
        }
    }
}

actual fun getFilePicker(): FilePicker = JvmFilePicker()
