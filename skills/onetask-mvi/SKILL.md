---
name: onetask-mvi
description: >
  MVI (Model-View-Intent) architecture rules for the OneTask project.
  Read this skill before creating or modifying any ViewModel, UI state, intent/event,
  or side-effect in this codebase. Covers naming, state modeling, intent dispatch,
  effect handling, and anti-patterns to avoid.
---

# OneTask — MVI Architecture Rules

## Overview

OneTask uses **MVI (Model-View-Intent)** as its presentation layer architecture.
Every screen has three contracts defined in a single `<Screen>Mvi.kt` file:

| Contract | Kotlin type | Role |
|---|---|---|
| **State** | `data class <Screen>UiState` | Immutable snapshot of everything the UI needs to render |
| **Intent** | `sealed interface <Screen>Intent` | All actions the user can trigger (or the system fires) |
| **Effect** | `sealed interface <Screen>Effect` | One-shot side effects that the UI must handle exactly once |

The ViewModel bridges them:
- Consumes `Intent` → updates `State` → emits `Effect` when needed.

---

## File Layout

```
shared/src/commonMain/kotlin/com/one/task/presentation/
├── mvi/
│   ├── AppMvi.kt          ← State + Intent + Effect contracts for the workspace
│   └── AppViewModel.kt    ← ViewModel for the workspace
└── ui/
    └── screens/
        └── WorkspaceScreen.kt  ← Compose UI (read-only view of State)
```

For a new screen `Foo`:
```
mvi/
├── FooMvi.kt        ← FooUiState, FooIntent, FooEffect
└── FooViewModel.kt  ← FooViewModel : ViewModel()
ui/screens/
└── FooScreen.kt
```

---

## 1. State — `data class <Screen>UiState`

### Rules
- **Immutable** — `data class` with `val` properties only.
- **Complete** — every piece of data the UI needs is in here; no local view state that affects business logic.
- **Default values** — provide sensible defaults so the initial state is always valid.
- Use `isLoading: Boolean = false` for any async operation.
- Use `error: String? = null` for error messages to show in the UI.

### Template

```kotlin
// AppMvi.kt — inside com.one.task.presentation.mvi
data class AppUiState(
    // Data
    val notebooks: List<Notebook> = emptyList(),
    val activeNotebookId: String? = null,
    val categoriesForActiveNotebook: List<Category> = emptyList(),
    val pagesForActiveNotebook: List<Page> = emptyList(),
    val activePageId: String? = null,
    val activePageBlocks: List<ContentBlock> = emptyList(),

    // Async / error
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### Updating State in the ViewModel

Always use `_state.update { it.copy(…) }` — never replace the whole object or mutate fields.

```kotlin
// ✅ Correct
_state.update { it.copy(activePageId = pageId) }
_state.update { it.copy(isLoading = true, error = null) }
_state.update { it.copy(notebooks = newList, isLoading = false) }

// ❌ Wrong — direct mutation
_state.value = AppUiState()           // loses all other fields
_state.value.activePageId = pageId   // impossible (val), but don't try
```

---

## 2. Intent — `sealed interface <Screen>Intent`

### Rules
- Every **user action** and **system trigger** is an Intent. Nothing else enters the ViewModel.
- Use `data class` for intents with parameters, `data object` for parameterless ones.
- Name intents as **imperative verbs**: `SelectPage`, `CreateNotebook`, `DeleteCategory`, `LoadInitialData`.
- No UI logic inside Intent classes — they are pure data carriers.

### Template

```kotlin
sealed interface AppIntent {
    // Parameterless triggers
    data object LoadInitialData : AppIntent

    // With payload
    data class SelectNotebook(val notebookId: String) : AppIntent
    data class SelectPage(val pageId: String) : AppIntent
    data class UpdateBlock(val block: ContentBlock) : AppIntent
    data class CreateNotebook(
        val name: String,
        val iconName: String,
        val colorHex: String,
        val isPrivate: Boolean
    ) : AppIntent
    data class CreateCategory(val name: String) : AppIntent
    data class CreatePage(
        val notebookId: String,
        val categoryId: String?,
        val title: String
    ) : AppIntent
    data class MovePageToCategory(val pageId: String, val categoryId: String?) : AppIntent
    data class DeleteCategory(val categoryId: String) : AppIntent
    data class DeletePage(val pageId: String) : AppIntent
}
```

### Dispatching Intents from the UI

```kotlin
// WorkspaceScreen.kt — UI only dispatches, never reads ViewModel internals
viewModel.onIntent(AppIntent.SelectPage(page.id))
viewModel.onIntent(AppIntent.CreateNotebook(name, iconName, colorHex, isPrivate))
viewModel.onIntent(AppIntent.DeletePage(pageId))

// ✅ Never call private ViewModel functions from the UI
// ❌ viewModel.loadInitialData()  ← wrong
// ❌ viewModel.repository.getAll() ← wrong
```

---

## 3. Effect — `sealed interface <Screen>Effect`

### Rules
- Effects are **one-shot events** — things that happen once and are not stored in State.
- Typical examples: show a snackbar/toast, navigate to another screen, open a dialog.
- Use `MutableSharedFlow` (replay = 0) so the UI collects them as events, not as state.
- Collect effects in a `LaunchedEffect` inside the Compose screen.

### Template

```kotlin
// In <Screen>Mvi.kt
sealed interface AppEffect {
    data class ShowToast(val message: String) : AppEffect
    data class NavigateTo(val route: String) : AppEffect
    data object ShowCreatePageDialog : AppEffect
}
```

### Emitting Effects from ViewModel

```kotlin
private val _effects = MutableSharedFlow<AppEffect>()
val effects = _effects.asSharedFlow()

// Inside a private function in the ViewModel:
private fun deletePage(pageId: String) {
    viewModelScope.launch {
        repository.deletePage(pageId)
        _effects.emit(AppEffect.ShowToast("Page deleted"))
    }
}
```

### Collecting Effects in the UI

```kotlin
// WorkspaceScreen.kt or any @Composable screen
val context = LocalContext.current  // or platform equivalent

LaunchedEffect(viewModel) {
    viewModel.effects.collect { effect ->
        when (effect) {
            is AppEffect.ShowToast -> {
                // show snackbar or toast
            }
            is AppEffect.NavigateTo -> {
                // navController.navigate(effect.route)
            }
        }
    }
}
```

---

## 4. ViewModel — `<Screen>ViewModel : ViewModel()`

### Rules
- Extends `androidx.lifecycle.ViewModel` (from `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose`).
- Use `viewModelScope` for all coroutines — it auto-cancels when the ViewModel is cleared.
- The **only public API** is: `val state: StateFlow`, `val effects: SharedFlow`, `fun onIntent(intent)`.
- All private business logic functions are called **only from `onIntent()`**.
- Keep `onIntent()` as a flat `when` dispatcher — no logic inside it.

### Template

```kotlin
class AppViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AppEffect>()
    val effects: SharedFlow<AppEffect> = _effects.asSharedFlow()

    init {
        onIntent(AppIntent.LoadInitialData)
    }

    fun onIntent(intent: AppIntent) {
        when (intent) {
            is AppIntent.LoadInitialData       -> loadInitialData()
            is AppIntent.SelectNotebook        -> selectNotebook(intent.notebookId)
            is AppIntent.SelectPage            -> selectPage(intent.pageId)
            is AppIntent.UpdateBlock           -> updateBlock(intent.block)
            is AppIntent.CreateNotebook        -> createNotebook(intent.name, intent.iconName, intent.colorHex, intent.isPrivate)
            is AppIntent.CreateCategory        -> createCategory(intent.name)
            is AppIntent.CreatePage            -> createPage(intent.notebookId, intent.categoryId, intent.title)
            is AppIntent.MovePageToCategory    -> movePageToCategory(intent.pageId, intent.categoryId)
            is AppIntent.DeleteCategory        -> deleteCategory(intent.categoryId)
            is AppIntent.DeletePage            -> deletePage(intent.pageId)
        }
    }

    // ── Private handlers ──────────────────────────────────────────────────

    private fun loadInitialData() {
        viewModelScope.launch {
            repository.getAllNotebooks().collect { notebooks ->
                _state.update { it.copy(notebooks = notebooks) }
            }
        }
    }

    private fun selectPage(pageId: String) {
        _state.update { it.copy(activePageId = pageId) }
        viewModelScope.launch {
            repository.getBlocksForPage(pageId).collect { blocks ->
                _state.update { it.copy(activePageBlocks = blocks.sortedBy { b -> b.sortOrder }) }
            }
        }
    }
}
```

---

## 5. Compose UI — View Layer Rules

### Rules
- The UI **only reads** `state` and **only writes** via `onIntent()`. Nothing else.
- Derive all display values from `state` — never cache or shadow them in local `remember`.
- Local UI state (e.g., dialog visibility, hover animation) that does **not** affect business logic is allowed as `remember { mutableStateOf(…) }`.
- Collect `state` using `collectAsState()`.

```kotlin
@Composable
fun WorkspaceScreen(viewModel: AppViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    // ✅ Local UI-only state (dialog visibility is purely UI)
    var showCreateDialog by remember { mutableStateOf(false) }

    // ✅ Collect effects once
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AppEffect.ShowToast -> { /* … */ }
            }
        }
    }

    // ✅ Dispatch intents on user actions
    Button(onClick = { viewModel.onIntent(AppIntent.CreatePage(nbId, null, "New Page")) }) {
        Text(stringResource(Res.string.btn_create))
    }

    // ✅ Read from state
    Text(state.notebooks.size.toString())
}
```

---

## 6. Adding a New Screen — Step-by-Step Checklist

1. **Create `<Screen>Mvi.kt`** in `commonMain/.../presentation/mvi/`
   - Define `<Screen>UiState` data class with defaults
   - Define `sealed interface <Screen>Intent` — all user + system intents
   - Define `sealed interface <Screen>Effect` — navigation + one-shot events

2. **Create `<Screen>ViewModel.kt`** in the same package
   - Extend `ViewModel()`
   - Expose `state: StateFlow`, `effects: SharedFlow`, `onIntent()`
   - Implement a private handler for every intent branch

3. **Register in Koin** in `AppModule.kt`
   ```kotlin
   factory { <Screen>ViewModel(get()) }
   ```

4. **Create `<Screen>Screen.kt`** in `commonMain/.../presentation/ui/screens/`
   - Obtain ViewModel via `koinViewModel()`
   - Collect `state` with `collectAsState()`
   - Collect `effects` in `LaunchedEffect`
   - Pass `onIntent` callbacks down to child components (never the full ViewModel)

5. **Never skip the contracts file** — even trivial screens get a `<Screen>Mvi.kt`.

---

## 7. Anti-Patterns — Never Do These

```kotlin
// ❌ Business logic in the UI
val filtered = state.pages.filter { it.categoryId == selectedId }  // belongs in State or ViewModel

// ❌ Calling repository directly from UI
val data = remember { repository.getAllNotebooks() }

// ❌ Replacing state instead of copying
_state.value = AppUiState()  // wipes all other state fields

// ❌ Mutable state inside a sealed class
data class AppIntent.CreatePage(var title: String)  // must be val

// ❌ Emitting navigation as State
data class AppUiState(val navigateToPage: String? = null)  // use Effect instead

// ❌ Exposing MutableStateFlow publicly
val state = MutableStateFlow(AppUiState())  // must be private _state, exposed as asStateFlow()

// ❌ Multiple flows for the same screen
val notebooksFlow = repository.getAllNotebooks()   // collect inside ViewModel, merge into _state
```

---

## 8. Current Contracts Reference

### `AppUiState`
```kotlin
data class AppUiState(
    val notebooks: List<Notebook> = emptyList(),
    val activeNotebookId: String? = null,
    val categoriesForActiveNotebook: List<Category> = emptyList(),
    val pagesForActiveNotebook: List<Page> = emptyList(),
    val activePageId: String? = null,
    val activePageBlocks: List<ContentBlock> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### `AppIntent`
`LoadInitialData` · `SelectNotebook(notebookId)` · `SelectPage(pageId)` · `UpdateBlock(block)` ·
`CreateNotebook(name, iconName, colorHex, isPrivate)` · `CreateCategory(name)` ·
`CreatePage(notebookId, categoryId, title)` · `MovePageToCategory(pageId, categoryId)` ·
`DeleteCategory(categoryId)` · `DeletePage(pageId)`

### `AppEffect`
`ShowToast(message: String)`
