package com.one.task.presentation.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconHelper {
    val pageIcons = listOf(
        "Document",
        "Rocket",
        "Calendar",
        "Drafts",
        "Code",
        "Briefcase",
        "List",
        "CheckCircle",
        "Assignment",
        "Flag"
    )

    fun getIcon(name: String?): ImageVector {
        return when (name) {
            "Document" -> Icons.Outlined.Description
            "Rocket" -> Icons.Outlined.RocketLaunch
            "Calendar" -> Icons.Outlined.DateRange
            "Drafts" -> Icons.Outlined.EditNote
            "Code" -> Icons.Outlined.Code
            "Briefcase" -> Icons.Outlined.WorkOutline
            "List" -> Icons.AutoMirrored.Outlined.FormatListBulleted
            "CheckCircle" -> Icons.Outlined.CheckCircleOutline
            "Assignment" -> Icons.AutoMirrored.Outlined.Assignment
            "Flag" -> Icons.Outlined.Flag
            else -> Icons.Outlined.Description
        }
    }

    val notebookIcons = listOf(
        "FolderSpecial",
        "Book",
        "Folder",
        "Lightbulb",
        "TrackChanges",
        "Code",
        "Palette"
    )

    fun getNotebookIcon(name: String?): ImageVector {
        return when (name) {
            "Book" -> Icons.Default.Book
            "Folder" -> Icons.Default.Folder
            "Lightbulb" -> Icons.Default.Lightbulb
            "TrackChanges" -> Icons.Default.TrackChanges
            "Code" -> Icons.Default.Code
            "Palette" -> Icons.Default.Palette
            "FolderSpecial" -> Icons.Default.FolderSpecial
            else -> Icons.Default.FolderSpecial
        }
    }
}
