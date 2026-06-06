package com.one.task.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.one.task.data.db.AppDatabase
import com.one.task.domain.*
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
    private val TAG = "TaskRepository"

    // Notebooks
    fun getAllNotebooks(): Flow<List<Notebook>> {
        return queries.getAllNotebooks().asFlow().mapToList(dispatcher).map { list ->
            Logger.d(TAG, "Fetched ${list.size} notebooks")
            list.map { Notebook(it.id, it.name, it.iconUrl, it.iconName, it.colorHex, it.isPrivate == 1L) }
        }
    }

    suspend fun insertNotebook(notebook: Notebook) = withContext(dispatcher) {
        Logger.d(TAG, "Inserting notebook: ${notebook.name} (${notebook.id})")
        queries.insertNotebook(notebook.id, notebook.name, notebook.iconUrl, notebook.iconName, notebook.colorHex, if (notebook.isPrivate) 1L else 0L)
    }


    // Pages
    fun getPagesForNotebook(notebookId: String): Flow<List<Page>> {
        return queries.getPagesForNotebook(notebookId).asFlow().mapToList(dispatcher).map { list ->
            Logger.d(TAG, "Fetched ${list.size} pages for notebook: $notebookId")
            list.map { entity ->
                val tags: List<String> = try {
                    json.decodeFromString(entity.tags)
                } catch (e: Exception) {
                    Logger.e(TAG, "Error decoding tags for page: ${entity.id}", e)
                    emptyList()
                }
                Page(entity.id, entity.notebookId, entity.title, entity.description, entity.iconName, entity.updatedAt, tags, entity.isArchived == 1L)
            }
        }
    }

    fun getArchivedPages(): Flow<List<Page>> {
        return queries.getArchivedPages().asFlow().mapToList(dispatcher).map { list ->
            Logger.d(TAG, "Fetched ${list.size} archived pages")
            list.map { entity ->
                val tags: List<String> = try {
                    json.decodeFromString(entity.tags)
                } catch (e: Exception) {
                    Logger.e(TAG, "Error decoding tags for archived page: ${entity.id}", e)
                    emptyList()
                }
                Page(entity.id, entity.notebookId, entity.title, entity.description, entity.iconName, entity.updatedAt, tags, entity.isArchived == 1L)
            }
        }
    }

    suspend fun insertPage(page: Page) = withContext(dispatcher) {
        Logger.d(TAG, "Inserting page: ${page.title} (${page.id}) in notebook: ${page.notebookId}")
        val tagsJson = json.encodeToString(page.tags)
        queries.insertPage(page.id, page.notebookId, page.title, page.description, page.iconName, page.updatedAt, tagsJson, if (page.isArchived) 1L else 0L)
    }

    suspend fun updatePageTitle(pageId: String, title: String, updatedAt: Long) = withContext(dispatcher) {
        Logger.d(TAG, "Updating title for page $pageId to: $title")
        queries.updatePageTitle(title, updatedAt, pageId)
    }

    suspend fun updatePageDescription(pageId: String, description: String?, updatedAt: Long) = withContext(dispatcher) {
        Logger.d(TAG, "Updating description for page $pageId")
        queries.updatePageDescription(description, updatedAt, pageId)
    }

    suspend fun updatePageTags(pageId: String, tags: List<String>, updatedAt: Long) = withContext(dispatcher) {
        Logger.d(TAG, "Updating tags for page $pageId: $tags")
        val tagsJson = json.encodeToString(tags)
        queries.updatePageTags(tagsJson, updatedAt, pageId)
    }

    suspend fun archivePage(pageId: String, updatedAt: Long) = withContext(dispatcher) {
        Logger.d(TAG, "Archiving page $pageId")
        queries.archivePage(updatedAt, pageId)
    }

    suspend fun restorePage(pageId: String, updatedAt: Long) = withContext(dispatcher) {
        Logger.d(TAG, "Restoring page $pageId")
        queries.restorePage(updatedAt, pageId)
    }

    suspend fun deletePage(pageId: String) = withContext(dispatcher) {
        Logger.d(TAG, "Deleting page $pageId")
        queries.deletePage(pageId)
    }

    suspend fun deleteAllArchivedPages() = withContext(dispatcher) {
        Logger.d(TAG, "Emptying archive")
        queries.deleteAllArchivedPages()
    }

    suspend fun clearAllData() = withContext(dispatcher) {
        Logger.d(TAG, "Clearing all data")
        queries.deleteAllNotebooks()
    }

    suspend fun getFullWorkspaceBackup(): WorkspaceBackup = withContext(dispatcher) {
        val notebooks = queries.getAllNotebooks().executeAsList().map { n ->
            val notebook = Notebook(n.id, n.name, n.iconUrl, n.iconName, n.colorHex, n.isPrivate == 1L)
            val pages = queries.getPagesForNotebook(n.id).executeAsList().map { p ->
                val tags: List<String> = try { json.decodeFromString(p.tags) } catch (e: Exception) { emptyList() }
                val page = Page(p.id, p.notebookId, p.title, p.description, p.iconName, p.updatedAt, tags, p.isArchived == 1L)
                val blocks = queries.getBlocksForPage(p.id).executeAsList().map { b ->
                    json.decodeFromString<ContentBlock>(b.content)
                }
                PageBackup(page, blocks)
            }
            // Also get archived pages for this notebook (queries.getPagesForNotebook filters out archived)
            // Actually, queries.getArchivedPages is global. Let's just fetch all pages.
            val allPagesForNotebook = queries.transactionWithResult {
                val pEntities = database.appDatabaseQueries.getPagesForNotebook(n.id).executeAsList() + 
                                database.appDatabaseQueries.getArchivedPages().executeAsList().filter { it.notebookId == n.id }
                pEntities.distinctBy { it.id }.map { p ->
                    val tags: List<String> = try { json.decodeFromString(p.tags) } catch (e: Exception) { emptyList() }
                    val page = Page(p.id, p.notebookId, p.title, p.description, p.iconName, p.updatedAt, tags, p.isArchived == 1L)
                    val blocks = queries.getBlocksForPage(p.id).executeAsList().map { b ->
                        json.decodeFromString<ContentBlock>(b.content)
                    }
                    PageBackup(page, blocks)
                }
            }
            
            NotebookBackup(notebook, allPagesForNotebook)
        }
        WorkspaceBackup(notebooks)
    }

    suspend fun restoreFromBackup(backup: WorkspaceBackup) = withContext(dispatcher) {
        queries.transaction {
            queries.deleteAllNotebooks()
            backup.notebooks.forEach { nbBackup ->
                val nb = nbBackup.notebook
                queries.insertNotebook(nb.id, nb.name, nb.iconUrl, nb.iconName, nb.colorHex, if (nb.isPrivate) 1L else 0L)
                nbBackup.pages.forEach { pBackup ->
                    val p = pBackup.page
                    val tagsJson = json.encodeToString(p.tags)
                    queries.insertPage(p.id, p.notebookId, p.title, p.description, p.iconName, p.updatedAt, tagsJson, if (p.isArchived) 1L else 0L)
                    pBackup.blocks.forEach { block ->
                        val contentJson = json.encodeToString(block)
                        val type = block::class.simpleName ?: "Unknown"
                        queries.insertBlock(block.id, p.id, type, contentJson, block.sortOrder.toLong())
                    }
                }
            }
        }
    }

    // Blocks
    fun getBlocksForPage(pageId: String): Flow<List<ContentBlock>> {
        return queries.getBlocksForPage(pageId).asFlow().mapToList(dispatcher).map { list ->
            Logger.d(TAG, "Fetched ${list.size} blocks for page: $pageId")
            list.map { entity ->
                json.decodeFromString<ContentBlock>(entity.content)
            }
        }
    }

    suspend fun saveBlocksForPage(pageId: String, blocks: List<ContentBlock>) = withContext(dispatcher) {
        Logger.d(TAG, "Saving ${blocks.size} blocks for page: $pageId")
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
