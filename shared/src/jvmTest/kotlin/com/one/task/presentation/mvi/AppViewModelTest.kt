package com.one.task.presentation.mvi

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.one.task.data.TaskRepository
import com.one.task.data.db.AppDatabase
import com.one.task.domain.TextBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.MutableStateFlow
import com.one.task.data.SettingsRepository

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {
    private lateinit var repository: TaskRepository
    private lateinit var viewModel: AppViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)
        repository = TaskRepository(database, testDispatcher)
        viewModel = AppViewModel(repository, FakeSettingsRepository())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial data loading seeds data if empty`() = runTest {
        // AppViewModel init calls LoadInitialData
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.notebooks.size)
        assertEquals("Getting Started", state.notebooks[0].name)
        assertNotNull(state.activeNotebookId)
    }

    @Test
    fun `test create notebook`() = runTest {
        advanceUntilIdle()
        
        viewModel.onIntent(AppIntent.CreateNotebook("New Notebook", null, "#FFFFFF", false))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(2, state.notebooks.size)
        assertEquals("New Notebook", state.notebooks.find { it.name == "New Notebook" }?.name)
        assertEquals(state.notebooks.find { it.name == "New Notebook" }?.id, state.activeNotebookId)
    }

    @Test
    fun `test select notebook and page`() = runTest {
        advanceUntilIdle()
        
        val notebooks = repository.getAllNotebooks().first()
        val firstNbId = notebooks[0].id
        
        viewModel.onIntent(AppIntent.SelectNotebook(firstNbId))
        advanceUntilIdle()
        
        val state = viewModel.state.value
        assertEquals(firstNbId, state.activeNotebookId)
        assertNotNull(state.activePageId)
    }

    @Test
    fun `test add block to page`() = runTest {
        advanceUntilIdle()
        
        val activePageId = viewModel.state.value.activePageId!!
        val newBlock = TextBlock("new-block", 0, "Test Block Content")
        
        viewModel.onIntent(AppIntent.AddBlock(activePageId, newBlock))
        advanceUntilIdle()
        
        // Wait for the 500ms delay in runSaving
        advanceTimeBy(600.milliseconds)
        advanceUntilIdle()

        val blocks = viewModel.state.value.activePageBlocks
        // Initially there were 13 blocks seeded
        assertEquals(14, blocks.size)
        assertEquals("Test Block Content", (blocks.find { it.id == "new-block" } as TextBlock).text)
    }
}
