package com.one.task.presentation.mvi

import com.one.task.domain.ContentBlock
import com.one.task.domain.Notebook
import com.one.task.domain.Page

data class AppUiState(
    val notebooks: List<Notebook> = emptyList(),
    val activeNotebookId: String? = null,
    val pagesForActiveNotebook: List<Page> = emptyList(),
    val activePageId: String? = null,
    val activePageBlocks: List<ContentBlock> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface AppIntent {
    data class SelectNotebook(val notebookId: String) : AppIntent
    data class SelectPage(val pageId: String) : AppIntent
    data class UpdateBlock(val block: ContentBlock) : AppIntent
    data class CreateNotebook(val name: String, val iconName: String, val colorHex: String, val isPrivate: Boolean) : AppIntent
    data class CreatePage(val notebookId: String, val title: String) : AppIntent
    data object LoadInitialData : AppIntent
}

sealed interface AppEffect {
    data class ShowToast(val message: String) : AppEffect
}
