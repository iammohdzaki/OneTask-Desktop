package com.one.task.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.one.task.data.db.AppDatabase
import com.one.task.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TaskRepository(
    private val database: AppDatabase
) {
    private val queries = database.appDatabaseQueries
    
    // Notebooks
    fun getAllNotebooks(): Flow<List<Notebook>> {
        return queries.getAllNotebooks().asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { Notebook(it.id, it.name, it.iconUrl, it.iconName, it.colorHex, it.isPrivate == 1L) }
        }
    }

    suspend fun insertNotebook(notebook: Notebook) = withContext(Dispatchers.Default) {
        queries.insertNotebook(notebook.id, notebook.name, notebook.iconUrl, notebook.iconName, notebook.colorHex, if (notebook.isPrivate) 1L else 0L)
    }

    // Pages
    fun getPagesForNotebook(notebookId: String): Flow<List<Page>> {
        return queries.getPagesForNotebook(notebookId).asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { Page(it.id, it.notebookId, it.title, it.updatedAt) }
        }
    }

    suspend fun insertPage(page: Page) = withContext(Dispatchers.Default) {
        queries.insertPage(page.id, page.notebookId, page.title, page.updatedAt)
    }

    // Blocks
    fun getBlocksForPage(pageId: String): Flow<List<ContentBlock>> {
        return queries.getBlocksForPage(pageId).asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { entity ->
                Json.decodeFromString<ContentBlock>(entity.content)
            }
        }
    }

    suspend fun saveBlocksForPage(pageId: String, blocks: List<ContentBlock>) = withContext(Dispatchers.Default) {
        queries.transaction {
            queries.deleteBlocksForPage(pageId)
            blocks.forEach { block ->
                val contentJson = Json.encodeToString(block)
                val type = block::class.simpleName ?: "Unknown"
                queries.insertBlock(block.id, pageId, type, contentJson, block.sortOrder.toLong())
            }
        }
    }
}
