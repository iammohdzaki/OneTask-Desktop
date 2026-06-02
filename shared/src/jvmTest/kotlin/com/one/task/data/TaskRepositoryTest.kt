package com.one.task.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.one.task.data.db.AppDatabase
import com.one.task.domain.Notebook
import com.one.task.domain.Page
import com.one.task.domain.TextBlock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: TaskRepository

    @BeforeTest
    fun setup() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        database = AppDatabase(driver)
        repository = TaskRepository(database, kotlinx.coroutines.Dispatchers.Unconfined)
    }

    @Test
    fun `test insert and get notebooks`() = runBlocking {
        val notebook = Notebook("1", "Test Notebook", colorHex = "#FF0000")
        repository.insertNotebook(notebook)

        val notebooks = repository.getAllNotebooks().first()
        assertEquals(1, notebooks.size)
        assertEquals("Test Notebook", notebooks[0].name)
    }

    @Test
    fun `test insert and get pages`() = runBlocking {
        val notebook = Notebook("nb1", "Notebook 1")
        repository.insertNotebook(notebook)

        val page = Page("p1", "nb1", "Title", "Desc", "Icon", 12345L)
        repository.insertPage(page)

        val pages = repository.getPagesForNotebook("nb1").first()
        assertEquals(1, pages.size)
        assertEquals("Title", pages[0].title)
    }

    @Test
    fun `test archive and restore page`() = runBlocking {
        val notebook = Notebook("nb1", "Notebook 1")
        repository.insertNotebook(notebook)

        val page = Page("p1", "nb1", "Title", "Desc", "Icon", 12345L)
        repository.insertPage(page)

        repository.archivePage("p1", 12346L)
        
        val activePages = repository.getPagesForNotebook("nb1").first()
        assertTrue(activePages.isEmpty())

        val archivedPages = repository.getArchivedPages().first()
        assertEquals(1, archivedPages.size)
        assertEquals("p1", archivedPages[0].id)

        repository.restorePage("p1", 12347L)
        val activePagesAfterRestore = repository.getPagesForNotebook("nb1").first()
        assertEquals(1, activePagesAfterRestore.size)
    }

    @Test
    fun `test save and get blocks`() = runBlocking {
        val notebook = Notebook("nb1", "Notebook 1")
        repository.insertNotebook(notebook)

        val page = Page("p1", "nb1", "Title", "Desc", "Icon", 12345L)
        repository.insertPage(page)

        val block1 = TextBlock("b1", 0, "Hello")
        val block2 = TextBlock("b2", 1, "World")
        
        repository.saveBlocksForPage("p1", listOf(block1, block2))

        val blocks = repository.getBlocksForPage("p1").first()
        assertEquals(2, blocks.size)
        assertTrue(blocks[0] is TextBlock)
        assertEquals("Hello", (blocks[0] as TextBlock).text)
        assertEquals("World", (blocks[1] as TextBlock).text)
    }
}
