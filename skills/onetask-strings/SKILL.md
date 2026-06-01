---
name: onetask-strings
description: >
  String resource management rules for the OneTask project.
  Any time you add, modify, or review UI text in this project, read this skill first.
  Enforces the rule: no hardcoded UI strings in Kotlin/Compose files — all visible text
  must be defined in strings.xml and referenced via Compose Resources.
---

# OneTask — String Resource Rules

## The Golden Rule

**Every user-visible string that appears in the UI must be defined in `strings.xml`.**
Never write string literals directly inside `@Composable` functions, except:
- Log/debug messages (not shown to the user)
- Generated IDs or internal keys
- Compose `contentDescription` only when the value itself comes from `stringResource()`

---

## File Location

```
shared/src/commonMain/composeResources/values/strings.xml
```

This is the single source of truth for all UI strings. There is **no** per-language override yet — add to this file only.

---

## How to Add a New String

### 1. Define it in `strings.xml`

```xml
<!-- shared/src/commonMain/composeResources/values/strings.xml -->
<resources>
    <!-- Use snake_case for names. Group by screen/component with a comment. -->
    <string name="my_screen_title">My Screen</string>
    <string name="my_screen_empty_state">Nothing here yet. Create one!</string>
    <string name="btn_create">Create</string>
</resources>
```

**Naming convention:**

| Pattern | Example | Used for |
|---|---|---|
| `<screen>_<element>` | `workspace_title` | Screen-level labels |
| `btn_<action>` | `btn_create`, `btn_cancel` | Button labels |
| `hint_<field>` | `hint_type_description` | Placeholder / hint text |
| `content_desc_<icon>` | `content_desc_settings` | Icon `contentDescription` |
| `empty_<screen>_<element>` | `empty_page_title` | Empty state text |
| `dialog_<name>_<element>` | `dialog_create_notebook_title` | Dialog titles / labels |
| `sidebar_<element>` | `sidebar_pages`, `sidebar_archive` | Sidebar labels |
| `topbar_<element>` | `topbar_editor` | App bar labels |
| `table_header_<col>` | `table_header_name` | Table column headers |

---

### 2. Import and Use in Compose

```kotlin
// At the top of the file — import ALL from generated resources
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

// Inside a @Composable function
Text(
    text = stringResource(Res.string.my_screen_title),
    style = MaterialTheme.typography.titleLarge
)

// For contentDescription
Icon(
    imageVector = Icons.Default.Settings,
    contentDescription = stringResource(Res.string.content_desc_settings)
)

// For button labels
Button(onClick = { /* … */ }) {
    Text(stringResource(Res.string.btn_create))
}

// For hints / placeholders
BasicTextField(
    value = text,
    onValueChange = { text = it },
    decorationBox = { innerTextField ->
        if (text.isEmpty()) {
            Text(
                text = stringResource(Res.string.hint_type_description),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
        innerTextField()
    }
)
```

---

## Existing String Keys (Current Inventory)

These are already defined — **reference them, do not duplicate**:

| Key | Value | Used in |
|---|---|---|
| `app_name` | "OneTask" | App title |
| `sidebar_pages` | "Pages" | PagesSidebar header |
| `sidebar_archive` | "Archive" | PagesSidebar footer |
| `sidebar_trash` | "Trash" | PagesSidebar footer |
| `topbar_editor` | "Editor" | TopAppBar breadcrumb |
| `search_hint` | "Search in page..." | TopAppBar search placeholder |
| `empty_page_title` | "No Page Selected" | WorkspaceScreen top bar fallback |
| `empty_page_message` | "Select a page to view" | WorkspaceScreen empty state |
| `content_desc_book` | "Book" | Icon content description |
| `content_desc_search` | "Search" | Icon content description |
| `content_desc_check_circle` | "Check Circle" | Icon content description |
| `content_desc_settings` | "Settings" | RailSidebar settings icon |
| `content_desc_add` | "Add" | Icon content description |
| `content_desc_more` | "More" | Icon content description |
| `last_edited` | "Last edited 2m ago" | PagesSidebar subtitle |
| `hint_type_description` | "Type a description..." | Block editor hint |
| `hint_type_task` | "Type a new task..." | Checkbox block hint |
| `table_header_name` | "Name" | Table block column |
| `table_header_role` | "Role" | Table block column |
| `table_header_status` | "Status" | Table block column |
| `image_prefix` | "Image:" | ImageBlock prefix |
| `add_notebook` | "Add Notebook" | RailSidebar add button |

---

## How to Spot Violations

Search for these patterns in Kotlin files — each match is a potential violation:

```
# Hardcoded strings passed to Text(), Button labels, contentDescription, etc.
Text("some literal")
Text(text = "some literal")
contentDescription = "some literal"
Button { Text("Click me") }
placeholder = "Type here"
```

Use IDE's Find Usages or:
```bash
grep -rn 'Text("' shared/src/commonMain/kotlin
grep -rn 'contentDescription = "' shared/src/commonMain/kotlin
```

---

## Workflow When Editing a Component

1. Identify every user-visible string literal in the file.
2. For each one: check the **Existing String Keys** table above.
   - If it already exists → use `stringResource(Res.string.<key>)`.
   - If it doesn't exist → add it to `strings.xml` first, then reference it.
3. Never leave a raw string literal in a `@Composable` that the user will see.

---

## Known Remaining Violations (as of project setup)

These hardcoded strings exist in the codebase and need fixing:

| File | Line (approx.) | Hardcoded String | Suggested Key |
|---|---|---|---|
| `WorkspaceScreen.kt` | 83 | `"New Page"` | `page_default_title` |
| `WorkspaceScreen.kt` | 107 | `"Create a Notebook to get started"` | `empty_notebook_prompt` |
| `PagesSidebar.kt` | 111 | `"Add Page"` | `btn_add_page` |
| `PagesSidebar.kt` | 164 | `"UNCATEGORIZED"` | `category_uncategorized` |
| `PagesSidebar.kt` | 288 | `"Delete Category"` | `btn_delete_category` |
| `PagesSidebar.kt` | 382 | `"Delete Page"` | `btn_delete_page` |
| `RailSidebar.kt` | 65 | `"OT"` | `app_initials` |
| `TopAppBar.kt` | 46 | `"Back"` | `content_desc_back` |
| `CreateNotebookDialog.kt` | — | various | audit needed |
| `CreateCategoryDialog.kt` | — | various | audit needed |
