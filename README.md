<div align="center">

# 📚 GATE Papers — Android App & Study Companion

### *The Ultimate Question Paper & Preparation Hub for GATE CS & DA Aspirants*

[![Release](https://img.shields.io/github/v/release/PUSHPAK-JAISWAL/gate-papers?color=F97316&label=Download%20APK&logo=android)](https://github.com/PUSHPAK-JAISWAL/gate-papers/releases/latest)
[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-34D399?logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg?logo=github)](CONTRIBUTING.md)

<br/>

[🚀 **Visit Official Website & Download**](https://pushpak-jaiswal.github.io/gate-papers/) •
[📲 **Direct APK Download**](https://github.com/PUSHPAK-JAISWAL/gate-papers/releases/latest/download/GATE-Papers.apk) •
[📖 **Documentation**](#-table-of-contents) •
[🤝 **Contribute**](CONTRIBUTING.md)

</div>

---

## 🌟 Overview

**GATE Papers** is a fast, offline-first native Android application crafted specifically for engineering students and graduates preparing for:
- **GATE CS & IT** (Computer Science & Information Technology)
- **GATE DA** (Data Science & Artificial Intelligence)

Built with modern **Jetpack Compose (Material 3)**, **Kotlin Coroutines**, and **Room Database**, GATE Papers solves the everyday hassle of hunting for past year question papers across scattered websites, broken PDF links, and spam-loaded portals.

With a single tap, access authentic past year question papers (2019–2024), view them in a high-performance native PDF engine, track your solved status, and print or export papers directly from your device.

---

## ⚡ Key Features

| Feature | Description |
| :--- | :--- |
| 📑 **Complete CS & DA Repository** | Official past papers from 2019 to 2024 for Computer Science (CS) and Data Science & AI (DA), including multi-session sets. |
| 🚀 **High-Speed In-App PDF Reader** | Read question papers with multi-touch zoom, smooth page panning, page indicator, and full-screen reading mode without third-party ad-ridden PDF apps. |
| 📴 **Offline Caching & Fast Sync** | Papers are cached locally after first viewing. Study anywhere, anytime—even with spotty or zero internet connectivity. |
| 🔔 **In-App Update Alerts & Settings** | Automatic new release notification with "Update Now" or "Later" options, accessible anytime via the Settings menu. |
| 🤖 **Automated Dependabot Lifecycle** | Full automated weekly dependency security updates for Gradle Version Catalog, GitHub Actions, and Web portal. |
| ✅ **Preparation Progress Tracker** | Mark solved papers as **Finished** or keep them **Pending**. Persistent local Room database tracks your exact prep completion. |
| 🔍 **Instant Search & Filter** | Instant filter by examination track (**CS / DA**), year range, and session codes (Set 1, Set 2). |
| 🖨️ **Native Print & Export Support** | Direct integration with Android `PrintManager` allows 1-click PDF exporting, wireless printing, and physical study notes generation. |
| 🔒 **100% Free & Privacy-First** | No telemetry, zero tracking SDKs, no annoying ads, no login required. Bring the focus back to studying. |

---

## 📲 Download & Installation

### Option 1: Direct Download from Website (Recommended)
Visit the official interactive web portal:
👉 **[https://pushpak-jaiswal.github.io/gate-papers/](https://pushpak-jaiswal.github.io/gate-papers/)**
Click **"Download APK"** to get the latest signed build directly.

### Option 2: GitHub Releases
Grab the latest release APK from our releases page:
1. Head to [Latest Releases](https://github.com/PUSHPAK-JAISWAL/gate-papers/releases/latest).
2. Download `GATE-Papers.apk`.
3. Open the downloaded file on your Android smartphone or tablet.
4. If prompted, allow **"Install from unknown sources"** for your browser or file manager.
5. Tap **Install** and launch **GATE Papers**!

---

## 🏗️ Architecture & Tech Stack

```
com.example
├── data
│   ├── local
│   │   ├── AppDatabase.kt        // Room Database configuration
│   │   ├── PaperDao.kt           // Data Access Object with Flow queries
│   │   └── PaperEntity.kt        // Paper schema with status, cache, & metadata
│   ├── model
│   │   └── PaperItem.kt          // Domain model
│   └── repository
│       └── PaperRepository.kt    // Single source of truth (Network + Local Cache)
└── ui
    ├── components
    │   ├── PdfViewer.kt          // Native Android PdfRenderer + Touch Canvas
    │   ├── PaperCard.kt          // Material 3 Paper Item with status toggle
    │   └── StatsCard.kt          // Preparation progress visualizer
    ├── screens
    │   ├── HomeScreen.kt         // Main catalog with filters & search
    │   └── PdfViewerScreen.kt    // Reader with zoom, pan, and printing
    └── theme
        ├── Color.kt              // Deep Dark Canvas & Vibrant Gate Accents
        └── Theme.kt              // Material 3 Dynamic / Custom Dark Scheme
```

- **UI Layer**: 100% Declarative Jetpack Compose with Material Design 3.
- **State Management**: `ViewModel` + `StateFlow` + `collectAsStateWithLifecycle`.
- **Concurrency**: Kotlin Coroutines & Flow.
- **Persistence**: Room SQLite ORM with local indexing.
- **Rendering**: Android Native `PdfRenderer` + multi-touch pan & zoom gesture detection.
- **Printing**: Native Android `PrintManager` and `PrintDocumentAdapter`.

---

## 💻 Building from Source

### Prerequisites
- Android Studio Ladybug (2024.2+) or later
- JDK 17
- Android SDK Platform 36

### Build Steps
```bash
# 1. Clone repository
git clone https://github.com/PUSHPAK-JAISWAL/gate-papers.git
cd gate-papers

# 2. Make gradlew executable
chmod +x gradlew

# 3. Build Debug APK
./gradlew assembleDebug

# 4. Run Unit & JVM tests
./gradlew testDebugUnitTest
```
The output APK will be placed at `app/build/outputs/apk/debug/app-debug.apk`.

---

## 🌐 Showcase Landing Website

The project includes an interactive React.js + Tailwind CSS showcase website located in the `website/` directory. It features:
- Live interactive mock of the app UI
- Direct APK download
- Paper catalog preview with syllabus highlights
- Comprehensive FAQ for aspirants
- Automatic GitHub Actions deployment via `.github/workflows/deploy.yml`

---

## 🤝 Contributing

Contributions are warmly welcomed! Whether you want to add verified answer keys, improve the PDF rendering pipeline, or submit new study features:
1. Review the [Contributing Guide](CONTRIBUTING.md).
2. Follow our [Code of Conduct](CODE_OF_CONDUCT.md).
3. Open a [Pull Request](https://github.com/PUSHPAK-JAISWAL/gate-papers/pulls).

---

## 🛡️ Security

If you discover any security vulnerabilities or concerns, please review our [Security Policy](SECURITY.md) and report them directly to [pushpakmjaiswal@gmail.com](mailto:pushpakmjaiswal@gmail.com).

---

## 📜 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author & Maintainer

<table align="center">
  <tr>
    <td align="center">
      <a href="https://github.com/PUSHPAK-JAISWAL">
        <img src="https://github.com/PUSHPAK-JAISWAL.png" width="100px;" alt="Pushpak Jaiswal"/><br />
        <sub><b>Pushpak Jaiswal</b></sub>
      </a><br />
      <sub>Creator & Lead Maintainer</sub><br />
      <a href="mailto:pushpakmjaiswal@gmail.com">✉️ pushpakmjaiswal@gmail.com</a> •
      <a href="https://github.com/PUSHPAK-JAISWAL">🐙 @PUSHPAK-JAISWAL</a>
    </td>
  </tr>
</table>

<div align="center">
  <b>If you found this project helpful, please give it a ⭐ on GitHub!</b>
</div>
