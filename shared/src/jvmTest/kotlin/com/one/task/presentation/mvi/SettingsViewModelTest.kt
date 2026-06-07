package com.one.task.presentation.mvi

import com.one.task.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeSettingsRepository : SettingsRepository {
    override val themeMode = MutableStateFlow("System")
    override val passwordAuthEnabled = MutableStateFlow(false)
    override val fontSize = MutableStateFlow(16)
    override val fullWidthEditor = MutableStateFlow(false)
    override val showLineNumbers = MutableStateFlow(false)
    override val autoSave = MutableStateFlow(true)
    override val hasSeededInitialData = MutableStateFlow(false)
    override val databasePath = MutableStateFlow<String?>(null)

    override suspend fun setThemeMode(mode: String) { themeMode.value = mode }
    override suspend fun setPasswordAuthEnabled(enabled: Boolean) { passwordAuthEnabled.value = enabled }
    override suspend fun setFontSize(size: Int) { fontSize.value = size }
    override suspend fun setFullWidthEditor(enabled: Boolean) { fullWidthEditor.value = enabled }
    override suspend fun setShowLineNumbers(enabled: Boolean) { showLineNumbers.value = enabled }
    override suspend fun setAutoSave(enabled: Boolean) { autoSave.value = enabled }
    override suspend fun setHasSeededInitialData(hasSeeded: Boolean) { hasSeededInitialData.value = hasSeeded }
    override suspend fun setDatabasePath(path: String?) { databasePath.value = path }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private lateinit var repository: FakeSettingsRepository
    private lateinit var taskRepository: com.one.task.data.TaskRepository
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeSettingsRepository()
        val driver = app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver(app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver.IN_MEMORY)
        com.one.task.data.db.AppDatabase.Schema.create(driver)
        taskRepository = com.one.task.data.TaskRepository(com.one.task.data.db.AppDatabase(driver), testDispatcher)
        viewModel = SettingsViewModel(repository, taskRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial settings state`() = runTest(testDispatcher) {
        val state = viewModel.state.value
        assertEquals("System", state.themeMode)
        assertEquals(false, state.passwordAuthEnabled)
    }

    @Test
    fun `test change theme mode`() = runTest(testDispatcher) {
        viewModel.onIntent(SettingsIntent.SetThemeMode("Dark"))
        assertEquals("Dark", viewModel.state.value.themeMode)
    }

    @Test
    fun `test toggle password auth`() = runTest(testDispatcher) {
        viewModel.onIntent(SettingsIntent.SetPasswordAuthEnabled(true))
        assertEquals(true, viewModel.state.value.passwordAuthEnabled)
    }
}
