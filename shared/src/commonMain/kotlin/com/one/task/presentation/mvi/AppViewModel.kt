package com.one.task.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.one.task.data.TaskRepository
import com.one.task.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class AppViewModel(
    private val repository: TaskRepository,
    private val settingsRepository: com.one.task.data.SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AppUiState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AppEffect>()
    val effects = _effects.asSharedFlow()

    init {
        onIntent(AppIntent.LoadInitialData)
    }

    fun onIntent(intent: AppIntent) {
        when (intent) {
            is AppIntent.LoadInitialData -> loadInitialData()
            is AppIntent.SelectNotebook -> selectNotebook(intent.notebookId)
            is AppIntent.SelectPage -> selectPage(intent.pageId)
            is AppIntent.UpdateBlock -> updateBlock(intent.block)
            is AppIntent.CreateNotebook -> createNotebook(intent.name, intent.iconName, intent.colorHex, intent.isPrivate, intent.iconUrl)
            is AppIntent.CreatePage -> createPage(intent.notebookId, intent.title, intent.description, intent.iconName)
            is AppIntent.DeletePage -> deletePage(intent.pageId)
            is AppIntent.RenamePageTitle -> renamePageTitle(intent.pageId, intent.title)
            is AppIntent.RenamePageDescription -> renamePageDescription(intent.pageId, intent.description)
            is AppIntent.AddTag -> addTag(intent.pageId, intent.tag)
            is AppIntent.RemoveTag -> removeTag(intent.pageId, intent.tag)
            is AppIntent.AddBlock -> addBlock(intent.pageId, intent.block)
            is AppIntent.DeleteBlock -> deleteBlock(intent.pageId, intent.blockId)
            is AppIntent.ReorderBlocks -> reorderBlocks(intent.pageId, intent.blocks)
            is AppIntent.ArchivePage -> archivePage(intent.pageId)
            is AppIntent.RestorePage -> restorePage(intent.pageId)
            is AppIntent.EmptyArchive -> emptyArchive()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            repository.getArchivedPages().collect { archived ->
                _state.update { it.copy(archivedPages = archived) }
            }
        }
        viewModelScope.launch {
            repository.getAllNotebooks().collect { notebooks ->
                _state.update { it.copy(notebooks = notebooks) }
                
                val hasSeeded = settingsRepository.hasSeededInitialData.first()
                if (notebooks.isEmpty() && !hasSeeded) {
                    // Seed initial data for new users
                    val nb = Notebook(generateUuid(), "Getting Started", iconName = "Lightbulb", colorHex = "#FFB869", isPrivate = false)
                    repository.insertNotebook(nb)
                    
                    val p1 = Page(generateUuid(), nb.id, "Welcome to OneTask", "Your personal workspace.", "Rocket", currentTimeMillis())
                    repository.insertPage(p1)
                    
                    val blocks = InitialData.getGettingStartedBlocks()
                    
                    repository.saveBlocksForPage(p1.id, blocks)
                    settingsRepository.setHasSeededInitialData(true)
                } else if (_state.value.activeNotebookId == null && notebooks.isNotEmpty()) {
                    selectNotebook(notebooks.first().id)
                }
            }
        }
    }

    private var pagesJob: kotlinx.coroutines.Job? = null
    private fun selectNotebook(notebookId: String) {
        _state.update { it.copy(activeNotebookId = notebookId, activePageId = null, activePageBlocks = emptyList()) }
        pagesJob?.cancel()
        pagesJob = viewModelScope.launch {
            repository.getPagesForNotebook(notebookId).collect { pages ->
                _state.update { it.copy(pagesForActiveNotebook = pages) }
                if (pages.isNotEmpty() && _state.value.activePageId == null) {
                    selectPage(pages.first().id)
                }
            }
        }
    }

    private var blocksJob: kotlinx.coroutines.Job? = null
    private fun selectPage(pageId: String) {
        _state.update { it.copy(activePageId = pageId) }
        blocksJob?.cancel()
        blocksJob = viewModelScope.launch {
            repository.getBlocksForPage(pageId).collect { blocks ->
                _state.update { it.copy(activePageBlocks = blocks.sortedBy { it.sortOrder }) }
            }
        }
    }

    private fun updateBlock(block: ContentBlock) {
        runSaving {
            val pageId = _state.value.activePageId ?: return@runSaving
            val currentBlocks = _state.value.activePageBlocks.toMutableList()
            val index = currentBlocks.indexOfFirst { it.id == block.id }
            if (index != -1) {
                currentBlocks[index] = block
            } else {
                currentBlocks.add(block)
            }
            repository.saveBlocksForPage(pageId, currentBlocks)
        }
    }

    private fun createNotebook(name: String, iconName: String?, colorHex: String, isPrivate: Boolean, iconUrl: String? = null) {
        runSaving {
            val nb = Notebook(id = generateUuid(), name = name, iconName = iconName, colorHex = colorHex, isPrivate = isPrivate, iconUrl = iconUrl)
            repository.insertNotebook(nb)
            selectNotebook(nb.id)
        }
    }

    private fun createPage(notebookId: String, title: String, description: String?, iconName: String?) {
        runSaving {
            val page = Page(generateUuid(), notebookId, title, description, iconName, currentTimeMillis())
            repository.insertPage(page)
            selectPage(page.id)
        }
    }

    private fun archivePage(pageId: String) {
        runSaving {
            if (_state.value.activePageId == pageId) {
                _state.update { it.copy(activePageId = null, activePageBlocks = emptyList()) }
            }
            repository.archivePage(pageId, currentTimeMillis())
        }
    }

    private fun restorePage(pageId: String) {
        runSaving {
            repository.restorePage(pageId, currentTimeMillis())
        }
    }

    private fun emptyArchive() {
        runSaving {
            repository.deleteAllArchivedPages()
        }
    }

    private fun deletePage(pageId: String) {
        runSaving {
            if (_state.value.activePageId == pageId) {
                _state.update { it.copy(activePageId = null, activePageBlocks = emptyList()) }
            }
            repository.deletePage(pageId)
        }
    }

    private fun renamePageTitle(pageId: String, title: String) {
        runSaving {
            repository.updatePageTitle(pageId, title, currentTimeMillis())
        }
    }

    private fun renamePageDescription(pageId: String, description: String?) {
        runSaving {
            repository.updatePageDescription(pageId, description, currentTimeMillis())
        }
    }

    private fun addTag(pageId: String, tag: String) {
        runSaving {
            val page = _state.value.pagesForActiveNotebook.find { it.id == pageId } ?: return@runSaving
            val trimmed = tag.trim().removePrefix("#")
            if (trimmed.isBlank() || page.tags.contains(trimmed)) return@runSaving
            val newTags = page.tags + trimmed
            repository.updatePageTags(pageId, newTags, currentTimeMillis())
        }
    }

    private fun removeTag(pageId: String, tag: String) {
        runSaving {
            val page = _state.value.pagesForActiveNotebook.find { it.id == pageId } ?: return@runSaving
            val newTags = page.tags.filter { it != tag }
            repository.updatePageTags(pageId, newTags, currentTimeMillis())
        }
    }

    private fun addBlock(pageId: String, block: ContentBlock) {
        runSaving {
            val currentBlocks = _state.value.activePageBlocks.toMutableList()
            val maxOrder = currentBlocks.maxOfOrNull { it.sortOrder } ?: -1
            val orderedBlock = when (block) {
                is TextBlock     -> block.copy(sortOrder = maxOrder + 1)
                is CheckboxBlock -> block.copy(sortOrder = maxOrder + 1)
                is ImageBlock    -> block.copy(sortOrder = maxOrder + 1)
                is TableBlock    -> block.copy(sortOrder = maxOrder + 1)
                is HeadingBlock  -> block.copy(sortOrder = maxOrder + 1)
                is DividerBlock  -> block.copy(sortOrder = maxOrder + 1)
                is LinkBlock     -> block.copy(sortOrder = maxOrder + 1)
            }
            currentBlocks.add(orderedBlock)
            repository.saveBlocksForPage(pageId, currentBlocks)
        }
    }

    private fun deleteBlock(pageId: String, blockId: String) {
        runSaving {
            val currentBlocks = _state.value.activePageBlocks.filter { it.id != blockId }
            repository.saveBlocksForPage(pageId, currentBlocks)
        }
    }

    private fun reorderBlocks(pageId: String, blocks: List<ContentBlock>) {
        runSaving {
            repository.saveBlocksForPage(pageId, blocks)
        }
    }

    private fun runSaving(block: suspend () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            block()
            kotlinx.coroutines.delay(500.milliseconds)
            _state.update { it.copy(isSaving = false) }
        }
    }
}
