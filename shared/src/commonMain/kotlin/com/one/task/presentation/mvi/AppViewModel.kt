package com.one.task.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.one.task.data.TaskRepository
import com.one.task.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(
    private val repository: TaskRepository
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
            is AppIntent.CreateNotebook -> createNotebook(intent.name, intent.iconName, intent.colorHex, intent.isPrivate)
            is AppIntent.CreatePage -> createPage(intent.notebookId, intent.title)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            repository.getAllNotebooks().collect { notebooks ->
                _state.update { it.copy(notebooks = notebooks) }
                if (notebooks.isEmpty()) {
                    // Seed initial data for testing
                    val nb = Notebook(generateUuid(), "Project Phoenix", iconName = "FolderSpecial", colorHex = "#A078FF", isPrivate = false)
                    repository.insertNotebook(nb)
                    
                    val p1 = Page(generateUuid(), nb.id, "Architecture Roadmap", currentTimeMillis())
                    repository.insertPage(p1)
                    
                    val b1 = TextBlock(generateUuid(), 0, "Welcome to the new system.")
                    repository.saveBlocksForPage(p1.id, listOf(b1))
                } else if (_state.value.activeNotebookId == null) {
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
        viewModelScope.launch {
            val pageId = _state.value.activePageId ?: return@launch
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

    private fun createNotebook(name: String, iconName: String, colorHex: String, isPrivate: Boolean) {
        viewModelScope.launch {
            val nb = Notebook(id = generateUuid(), name = name, iconName = iconName, colorHex = colorHex, isPrivate = isPrivate)
            repository.insertNotebook(nb)
            selectNotebook(nb.id)
        }
    }

    private fun createPage(notebookId: String, title: String) {
        viewModelScope.launch {
            val page = Page(generateUuid(), notebookId, title, currentTimeMillis())
            repository.insertPage(page)
            selectPage(page.id)
        }
    }
}
