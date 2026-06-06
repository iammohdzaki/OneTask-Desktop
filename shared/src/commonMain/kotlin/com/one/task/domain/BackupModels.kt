package com.one.task.domain

import kotlinx.serialization.Serializable

@Serializable
data class WorkspaceBackup(
    val notebooks: List<NotebookBackup>,
    val version: Int = 1,
    val timestamp: Long = currentTimeMillis()
)

@Serializable
data class NotebookBackup(
    val notebook: Notebook,
    val pages: List<PageBackup>
)

@Serializable
data class PageBackup(
    val page: Page,
    val blocks: List<ContentBlock>
)
