<div align="center">
  <img src="images/logo.svg" alt="OneTask Logo" width="120" height="120" />
  <h1>OneTask</h1>
  <p><strong>A flexible, block-based workspace application built with Compose Multiplatform.</strong></p>

  [![GitHub License](https://img.shields.io/github/license/iammohdzaki/OneTask-Desktop?style=flat-square)](LICENSE)
  [![GitHub Release](https://img.shields.io/github/v/release/iammohdzaki/OneTask-Desktop?style=flat-square)](https://github.com/iammohdzaki/OneTask-Desktop/releases)
  [![Build Status](https://img.shields.io/github/actions/workflow/status/iammohdzaki/OneTask-Desktop/release.yml?style=flat-square)](https://github.com/iammohdzaki/OneTask-Desktop/actions/workflows/release.yml)
  ![Platform Support](https://img.shields.io/badge/platform-Windows%20%7C%20macOS%20%7C%20Linux-blue?style=flat-square)
</div>

<br/>

OneTask allows you to organize your thoughts, tasks, and data in a unified environment that feels local-first and high-performance.

<div align="center">
  <img src="images/screenshot.png" alt="OneTask Screenshot" style="border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);" />
</div>

## ✨ Features

- 🧱 **Block-Based Editor**: Build your pages using Text, Headings, Checkboxes, Tables, and Images.
- ✍️ **Rich Text Support**: Experience true WYSIWYG formatting including **Bold**, *Italic*, and __Underline__.
- 🚀 **Local-First Database**: Powered by SQLDelight for fast, responsive, and offline-first data persistence.
- 🖼️ **Optimized Image Loading**: High-performance image rendering and caching via Landscapist (Coil 3).
- 🧩 **MVI Architecture**: Built on robust state management for a predictable and smooth user experience.
- ☁️ **Bring Your Own Storage**: Sync across devices using Google Drive, Dropbox, or OneDrive.
- 🔒 **Privacy First**: All your data lives locally by default, giving you total control.

---

## ☁️ Cloud Sync

OneTask supports **Bring Your Own Storage (BYOS)** cloud sync. This means you can sync your data for free using your existing cloud provider (Google Drive, Dropbox, OneDrive, etc.) without needing a central server.

[Learn how to set up Cloud Sync →](docs/CLOUD_SYNC.md)

---

## 🚀 Installation

### Windows (Important)

Due to the lack of a paid digital signature (common in indie open-source projects), you may encounter two levels of Windows security:

#### 1. SmartScreen Warning
- When you run the installer, you may see a blue **"Windows protected your PC"** window.
- Click **"More info"** and then click **"Run anyway"**.

#### 2. Smart App Control (Windows 11)
- If you have **Smart App Control** enabled, Windows may block the app entirely with no "Run anyway" button.
- To install, you may need to:
    - **Option A**: Temporarily or permanently disable Smart App Control in **Windows Security > App & browser control > Smart App Control settings**.
    - **Option B**: Build and run from source (see [Building Locally](#building-locally) below), which bypasses installer checks.

---

### macOS
1. Download the latest `.dmg` from the Releases page.
2. Open the `.dmg` and drag **OneTask** into your **Applications** folder.

### Linux
1. Download the latest `.deb` package.
2. Install via your package manager: `sudo dpkg -i onetask_xxx.deb`.

---

## 🛠️ Building Locally

### Prerequisites
- JDK 17 or higher
- IntelliJ IDEA (recommended) or Android Studio

### Commands

To run the desktop application:
```bash
./gradlew :desktopApp:run
```

To build a production installer for your current OS:
```bash
./gradlew :desktopApp:package
```

---

## 🤝 Contributing

We welcome contributions! Please see our [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to get started.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
