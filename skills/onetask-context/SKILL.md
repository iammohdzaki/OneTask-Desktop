---
name: onetask-context
description: >
  High-level context and overview of the OneTask project.
  Read this to understand the project's purpose, architecture, 
  tech stack, and development constraints.
---

# OneTask — Project Context

## Project Overview
OneTask is a **local-first, privacy-focused productivity application** designed for knowledge management and note-taking. It allows users to organize their thoughts into Notebooks, Pages, and Blocks (similar to Notion or Obsidian).

### Core Principles
- **Privacy First:** Data is stored locally on the user's machine by default.
- **Local-First:** The app remains fully functional without an internet connection.
- **BYOS (Bring Your Own Storage):** Users can sync their data across devices by selecting a custom database location (e.g., inside a Dropbox or Google Drive folder).
- **Extensible Content:** Content is built using "Blocks" (Text, Heading, Task, Image, Table, etc.).

---

## Tech Stack
OneTask is built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**.

| Layer | Technology |
|---|---|
| **UI Framework** | Compose Multiplatform (Material 3) |
| **Architecture** | MVI (Model-View-Intent) |
| **Database** | SQLDelight (SQLite) |
| **Dependency Injection** | Koin |
| **Settings / Preferences** | DataStore (Preferences) |
| **Serialization** | Kotlinx Serialization (JSON) |
| **Logging** | SLF4J (JVM) with custom file-based persistent logging |

---

## Workspace Structure
```
OneTask/
├── desktopApp/          ← JVM/Desktop specific entry point and packaging
├── shared/              ← Core logic and UI (Multiplatform)
│   ├── commonMain/      ← Shared logic, MVI, and UI components
│   ├── jvmMain/         ← JVM-specific implementations (File IO, JDBC)
│   └── commonTest/      ← Logic and ViewModel tests
├── docs/                ← User and developer documentation
└── skills/              ← Specialized instructions for LLM agents
```

---

## Critical Development Workflows

### 1. Database & Migrations
- Uses `SQLDelight` for schema definition (`.sq` files).
- **Production Safety:** Database versions are tracked using `PRAGMA user_version`. 
- **Migration Policy:** Always update the version and implement `migrate` in `DriverFactory` when changing the schema.
- **Environment Isolation:** 
  - **Debug:** Data is stored in `desktopApp/dev-data/`.
  - **Release:** Data is stored in `%APPDATA%\OneTask\`.

### 2. UI & Design
- Adheres to **Material 3** design guidelines.
- **No Hardcoded Strings:** All UI text must be defined in `strings.xml` and accessed via `Res.string`.
- **Custom Title Bar:** The app uses an undecorated window with a custom-built draggable title bar.

### 3. State Management (MVI)
- Every screen must follow the `Mvi.kt` + `ViewModel.kt` pattern.
- State is immutable and updated via `_state.update { copy(...) }`.
- User actions are dispatched as `Intents`.

---

## Key Features Reference
- **BYOS Cloud Sync:** Users can change the database location in Settings. This writes a path to `boot.properties` in the AppData folder, which is read synchronously on the next startup.
- **Backup/Restore:** Supports full workspace export and import via JSON.
- **Rich Text Blocks:** Uses a custom `VisualTransformation` for Markdown-like markers (`**bold**`, `*italic*`, etc.) though complex formatting is currently hidden/simplified.
- **Global Exception Handling:** Uncaught errors are caught in `main.kt` and logged to persistent files.
