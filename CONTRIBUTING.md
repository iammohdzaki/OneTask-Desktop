# Contributing to OneTask

Thank you for your interest in contributing to OneTask! We welcome bug reports, feature requests, and code contributions.

## How to Contribute

1. **Bug Reports**: If you find a bug, please use the [Bug Report Template](.github/ISSUE_TEMPLATE/bug_report.md).
2. **Feature Requests**: If you have an idea for a new feature, please use the [Feature Request Template](.github/ISSUE_TEMPLATE/feature_request.md).
3. **Pull Requests**:
   - Fork the repository and create your branch from `main`.
   - Ensure your code follows the existing style and architecture.
   - Run tests to ensure no regressions are introduced.

## Development Setup

### Prerequisites
- **JDK 17**: Ensure you have the Java 17 Development Kit installed.
- **IntelliJ IDEA**: We recommend using IntelliJ for the best Compose Multiplatform development experience.

### Architecture Guidelines
- **MVI (Model-View-Intent)**: We follow strict MVI rules for state management. Read our internal architecture guide for more details.
- **String Resources**: Never hardcode UI strings. All text must be defined in `shared/src/commonMain/composeResources/values/strings.xml`.
- **Material 3**: Use Material 3 components and color tokens from the `Theme.kt` file.

### Running Tests
Before submitting a PR, please run the unit tests:
```bash
./gradlew :shared:allTests
```

## Code of Conduct
Please be respectful and professional in all interactions within this project.
