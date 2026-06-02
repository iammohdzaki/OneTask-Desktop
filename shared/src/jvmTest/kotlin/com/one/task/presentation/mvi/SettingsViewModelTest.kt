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

    override suspend fun setThemeMode(mode: String) {
        themeMode.value = mode
    }

    override suspend fun setPasswordAuthEnabled(enabled: Boolean) {
        passwordAuthEnabled.value = enabled
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private lateinit var repository: FakeSettingsRepository
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeSettingsRepository()
        viewModel = SettingsViewModel(repository)
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
