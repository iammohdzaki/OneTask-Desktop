# OneTask

A privacy-first, offline personal workspace application built with Kotlin Multiplatform and Compose Multiplatform. OneTask brings together a sleek, Notion-style user interface with a robust local-first architecture.

## Features

- **Offline-First by Design:** Powered by a local SQLite database using SQLDelight, ensuring your data never leaves your device unless you want it to.
- **Beautiful Adaptive UI:** Features a dynamic material-based interface with custom dark modes, interactive color and icon pickers, and fluid empty states.
- **MVI Architecture:** Predictable state management powered by Kotlin Coroutines (`StateFlow` / `SharedFlow`) and cleanly separated intents.
- **Modular Codebase:** Leverages Koin for Dependency Injection and adheres to strict Clean Architecture boundaries (Domain, Data, UI).
- **Cross-Platform Ready:** Built fundamentally with KMP principles. Currently optimized for Desktop (JVM), but architecture supports expanding to iOS, Android, and Web.

## Getting Started

### Prerequisites

- JDK 17 or higher
- IntelliJ IDEA or Android Studio (with the Kotlin Multiplatform plugin installed)

### Running the Desktop App

You can quickly launch the desktop application locally via Gradle:

```bash
# Standard run
./gradlew :desktopApp:run

# Hot reload (requires JetBrains Compose Plugin)
./gradlew :desktopApp:hotRun --auto
```

### Building for Distribution

To build native distribution packages (e.g. `.dmg`, `.msi`, `.deb`) for your operating system:

```bash
./gradlew :desktopApp:packageDistributionForCurrentOS
```
The packaged installers will be available in the `desktopApp/build/compose/binaries` directory.

## Project Structure

- `shared/` - Contains all business logic, local database layers (SQLDelight), ViewModels, MVI components, and shared UI (Compose Multiplatform).
- `desktopApp/` - The entry point for the desktop JVM application. Instantiates the window and supplies the driver to the shared architecture.
- `gradle/libs.versions.toml` - Version catalog for managing external dependencies across modules.