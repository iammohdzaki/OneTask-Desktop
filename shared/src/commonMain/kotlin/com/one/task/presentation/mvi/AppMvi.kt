package com.one.task.presentation.mvi


import com.one.task.domain.ContentBlock
import com.one.task.domain.Notebook
import com.one.task.domain.Page

data class AppUiState(
    val notebooks: List<Notebook> = emptyList(),
    val activeNotebookId: String? = null,

    val pagesForActiveNotebook: List<Page> = emptyList(),
    val archivedPages: List<Page> = emptyList(),
    val activePageId: String? = null,
    val activePageBlocks: List<ContentBlock> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed interface AppIntent {
    data class SelectNotebook(val notebookId: String) : AppIntent
    data class SelectPage(val pageId: String) : AppIntent
    data class UpdateBlock(val block: ContentBlock) : AppIntent
    data class CreateNotebook(val name: String, val iconName: String?, val colorHex: String, val isPrivate: Boolean, val iconUrl: String? = null) : AppIntent
    data class CreatePage(val notebookId: String, val title: String, val description: String?, val iconName: String?) : AppIntent
    data class ArchivePage(val pageId: String) : AppIntent
    data class RestorePage(val pageId: String) : AppIntent
    data class DeletePage(val pageId: String) : AppIntent
    data object EmptyArchive : AppIntent
    data object LoadInitialData : AppIntent

    // Editor panel upgrades
    data class RenamePageTitle(val pageId: String, val title: String) : AppIntent
    data class RenamePageDescription(val pageId: String, val description: String?) : AppIntent
    data class AddTag(val pageId: String, val tag: String) : AppIntent
    data class RemoveTag(val pageId: String, val tag: String) : AppIntent
    data class AddBlock(val pageId: String, val block: ContentBlock) : AppIntent
    data class DeleteBlock(val pageId: String, val blockId: String) : AppIntent
    data class ReorderBlocks(val pageId: String, val blocks: List<ContentBlock>) : AppIntent
}

sealed interface AppEffect {
    data class ShowToast(val message: String) : AppEffect
}
