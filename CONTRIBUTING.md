# Contributing to GATE Papers 🚀

Thank you for considering contributing to **GATE Papers**! Open source contributions make this app better for thousands of engineering students preparing for GATE CS (Computer Science & Information Technology) and GATE DA (Data Science & Artificial Intelligence).

---

## 📑 Table of Contents
1. [Code of Conduct](#code-of-conduct)
2. [How Can I Contribute?](#how-can-i-contribute)
   - [Reporting Bugs](#reporting-bugs)
   - [Suggesting Features / New Papers](#suggesting-features--new-papers)
   - [Pull Requests](#pull-requests)
3. [Local Development Setup](#local-development-setup)
4. [Coding Standards & Style Guide](#coding-standards--style-guide)
5. [Git Workflow](#git-workflow)
6. [Contact & Maintainers](#contact--maintainers)

---

## Code of Conduct

This project adheres to the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to **[pushpakmjaiswal@gmail.com](mailto:pushpakmjaiswal@gmail.com)**.

---

## How Can I Contribute?

### Reporting Bugs
If you find a bug or unexpected behavior:
1. Search existing [GitHub Issues](https://github.com/PUSHPAK-JAISWAL/gate-papers/issues) to verify it hasn't already been reported.
2. If not reported, open a new issue using the **Bug Report Template**.
3. Include:
   - Clear and descriptive title.
   - Steps to reproduce the bug.
   - Device model, Android OS version, and app version.
   - Screenshots or video recordings if applicable.

### Suggesting Features / New Papers
Have an idea to improve the app, or want to suggest verified official question papers / solutions?
- Open an issue using the **Feature Request Template**.
- Explain why this feature would be valuable for GATE aspirants.
- Provide official links or verified sources for question papers if suggesting new paper PDFs.

### Pull Requests
1. Fork the repository and create a branch from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. Keep your changes focused and minimal.
3. Test thoroughly on local JVM tests and verify build compilation:
   ```bash
   ./gradlew assembleDebug
   ./gradlew testDebugUnitTest
   ```
4. Submit a Pull Request targeting the `main` branch with a clear description using our [Pull Request Template](.github/pull_request_template.md).

---

## Local Development Setup

### Prerequisites
- **Android Studio Ladybug (2024.2+)** or later.
- **JDK 17** (Temurin or Corretto recommended).
- **Android SDK Platform 36** (API Level 36).
- Android device or emulator running **API 26+** (Android 8.0+).

### Quickstart
1. Clone your fork:
   ```bash
   git clone https://github.com/PUSHPAK-JAISWAL/gate-papers.git
   cd gate-papers
   ```
2. Open the project in Android Studio.
3. Let Gradle sync and resolve all dependencies.
4. Run the app on your device or emulator:
   ```bash
   ./gradlew installDebug
   ```

---

## Coding Standards & Style Guide

- **Language**: Kotlin only.
- **UI Framework**: Modern Jetpack Compose with Material Design 3 (M3).
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles.
- **Database**: Room (SQLite) with Kotlin Coroutines and StateFlow.
- **Color Theme**: Follow the established dark technical theme (`DarkBackground`, `SolidGateOrange`, `SolidGateCyan`, `SolidEmerald`).
- **Compose Rules**:
  - Keep Composables stateless where possible.
  - Hoist state to ViewModels using `MutableStateFlow` and `collectAsStateWithLifecycle()`.
  - Ensure minimum touch target of 48dp x 48dp for accessibility.
  - Provide `contentDescription` for all interactive iconography.

---

## Git Workflow

- **Branch Naming**:
  - `feature/paper-offline-viewer`
  - `fix/pdf-render-cache-crash`
  - `docs/update-installation-guide`
- **Commit Messages**: Follow [Conventional Commits](https://www.conventionalcommits.org/):
  - `feat: add year 2025 GATE DA question paper link`
  - `fix: correct aspect ratio on tablet landscape in PDF viewer`
  - `docs: update README with release APK instructions`

---

## Contact & Maintainers

- **Creator & Lead Maintainer**: Pushpak Jaiswal
- **GitHub**: [@PUSHPAK-JAISWAL](https://github.com/PUSHPAK-JAISWAL)
- **Email**: [pushpakmjaiswal@gmail.com](mailto:pushpakmjaiswal@gmail.com)

Thank you for helping empower GATE aspirants across the world! ⭐
