package com.one.task.domain

import kotlinx.serialization.Serializable

@Serializable
data class Notebook(
    val id: String,
    val name: String,
    val iconUrl: String? = null,
    val iconName: String? = null,
    val colorHex: String? = null,
    val isPrivate: Boolean = false
)

@Serializable
data class Page(
    val id: String,
    val notebookId: String,
    val title: String,
    val description: String? = null,
    val iconName: String? = null,
    val updatedAt: Long,
    val tags: List<String> = emptyList(),
    val isArchived: Boolean = false
)

@Serializable
sealed interface ContentBlock {
    val id: String
    val sortOrder: Int
}

@Serializable
data class TextBlock(
    override val id: String,
    override val sortOrder: Int,
    var text: String
) : ContentBlock

@Serializable
data class CheckboxBlock(
    override val id: String,
    override val sortOrder: Int,
    var text: String,
    var isChecked: Boolean,
    var tag: String? = null
) : ContentBlock

@Serializable
data class ImageBlock(
    override val id: String,
    override val sortOrder: Int,
    var localPath: String,
    var caption: String = "",
    var subtitle: String = "",
    var sizeMode: String = "Medium",
    var showCaption: Boolean = true,
    var url: String? = null
) : ContentBlock

@Serializable
data class TableBlock(
    override val id: String,
    override val sortOrder: Int,
    var title: String,
    var rows: Int,
    var cols: Int,
    var data: List<List<String>> = emptyList()
) : ContentBlock

@Serializable
data class HeadingBlock(
    override val id: String,
    override val sortOrder: Int,
    var level: Int = 1,
    var text: String = ""
) : ContentBlock

@Serializable
data class DividerBlock(
    override val id: String,
    override val sortOrder: Int
) : ContentBlock
