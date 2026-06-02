package com.one.task.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.one.task.data.db.AppDatabase
import com.one.task.domain.ContentBlock
import com.one.task.domain.Notebook
import com.one.task.domain.Page
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TaskRepository(
    private val database: AppDatabase,
    private val dispatcher: kotlinx.coroutines.CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default
) {
    private val queries = database.appDatabaseQueries
    private val json = Json { ignoreUnknownKeys = true }

    // Notebooks
    fun getAllNotebooks(): Flow<List<Notebook>> {
        return queries.getAllNotebooks().asFlow().mapToList(dispatcher).map { list ->
            list.map { Notebook(it.id, it.name, it.iconUrl, it.iconName, it.colorHex, it.isPrivate == 1L) }
        }
    }

    suspend fun insertNotebook(notebook: Notebook) = withContext(dispatcher) {
        queries.insertNotebook(notebook.id, notebook.name, notebook.iconUrl, notebook.iconName, notebook.colorHex, if (notebook.isPrivate) 1L else 0L)
    }


    // Pages
    fun getPagesForNotebook(notebookId: String): Flow<List<Page>> {
        return queries.getPagesForNotebook(notebookId).asFlow().mapToList(dispatcher).map { list ->
            list.map { entity ->
                val tags: List<String> = try {
                    json.decodeFromString(entity.tags)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
                Page(entity.id, entity.notebookId, entity.title, entity.description, entity.iconName, entity.updatedAt, tags, entity.isArchived == 1L)
            }
        }
    }

    fun getArchivedPages(): Flow<List<Page>> {
        return queries.getArchivedPages().asFlow().mapToList(dispatcher).map { list ->
            list.map { entity ->
                val tags: List<String> = try {
                    json.decodeFromString(entity.tags)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
                Page(entity.id, entity.notebookId, entity.title, entity.description, entity.iconName, entity.updatedAt, tags, entity.isArchived == 1L)
            }
        }
    }

    suspend fun insertPage(page: Page) = withContext(dispatcher) {
        val tagsJson = json.encodeToString(page.tags)
        queries.insertPage(page.id, page.notebookId, page.title, page.description, page.iconName, page.updatedAt, tagsJson, if (page.isArchived) 1L else 0L)
    }

    suspend fun updatePageTitle(pageId: String, title: String, updatedAt: Long) = withContext(dispatcher) {
        queries.updatePageTitle(title, updatedAt, pageId)
    }

    suspend fun updatePageDescription(pageId: String, description: String?, updatedAt: Long) = withContext(dispatcher) {
        queries.updatePageDescription(description, updatedAt, pageId)
    }

    suspend fun updatePageTags(pageId: String, tags: List<String>, updatedAt: Long) = withContext(dispatcher) {
        val tagsJson = json.encodeToString(tags)
        queries.updatePageTags(tagsJson, updatedAt, pageId)
    }

    suspend fun archivePage(pageId: String, updatedAt: Long) = withContext(dispatcher) {
        queries.archivePage(updatedAt, pageId)
    }

    suspend fun restorePage(pageId: String, updatedAt: Long) = withContext(dispatcher) {
        queries.restorePage(updatedAt, pageId)
    }

    suspend fun deletePage(pageId: String) = withContext(dispatcher) {
        queries.deletePage(pageId)
    }

    suspend fun deleteAllArchivedPages() = withContext(dispatcher) {
        queries.deleteAllArchivedPages()
    }

    // Blocks
    fun getBlocksForPage(pageId: String): Flow<List<ContentBlock>> {
        return queries.getBlocksForPage(pageId).asFlow().mapToList(dispatcher).map { list ->
            list.map { entity ->
                json.decodeFromString<ContentBlock>(entity.content)
            }
        }
    }

    suspend fun saveBlocksForPage(pageId: String, blocks: List<ContentBlock>) = withContext(dispatcher) {
        queries.transaction {
            queries.deleteBlocksForPage(pageId)
            blocks.forEach { block ->
                val contentJson = json.encodeToString(block)
                val type = block::class.simpleName ?: "Unknown"
                queries.insertBlock(block.id, pageId, type, contentJson, block.sortOrder.toLong())
            }
        }
    }
}
