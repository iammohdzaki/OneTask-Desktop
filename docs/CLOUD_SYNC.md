# Cloud Sync via Local Folder (The Obsidian Way)

OneTask allows you to sync your data across devices using your existing cloud storage (Google Drive, Dropbox, OneDrive, etc.) without needing a central server.

## How it works

Instead of storing your data in a hidden system folder, you can tell OneTask to save its database (`onetask.db`) into a folder that is already being synced by a cloud provider's desktop application.

## Setup Instructions

### 1. Install your Cloud Storage Desktop App
Ensure you have the desktop application for your preferred cloud service installed and running on your computer:
- [Google Drive for Desktop](https://www.google.com/drive/download/)
- [Dropbox](https://www.dropbox.com/install)
- [OneDrive](https://www.microsoft.com/en-us/microsoft-365/onedrive/download)

### 2. Change the Database Location in OneTask
1. Open OneTask.
2. Click on the **Settings** icon (bottom left).
3. Go to the **Data** tab.
4. Click on **Database Location**.
5. Select a folder inside your synced cloud directory (e.g., `G:\My Drive\OneTask` or `C:\Users\Name\Dropbox\OneTask`).
6. OneTask will show a message saying a restart is required.

### 3. Restart OneTask
Close and reopen the application. OneTask will now create and use the database file in your cloud-synced folder.

### 4. Setup on another Computer
To sync your data to a second computer:
1. Repeat Step 1 (Install the same cloud app).
2. Repeat Step 2 & 3 in OneTask, selecting the **exact same folder** you chose on the first computer.

## Why this is better
- **Privacy:** Your data never touches our servers. It stays between your PC and your cloud provider.
- **Cost:** It's 100% free if you already have cloud storage.
- **Ownership:** You have a physical file (`onetask.db`) that you can backup manually at any time.

## Troubleshooting

### Conflicts
If you have OneTask open on two computers at the exact same time and make changes, your cloud provider might create a "Conflicted Copy" of the database. To avoid this, try to close the app on one device before using it on another.

### Data not appearing
Ensure your cloud storage app is finished "Syncing" or "Uploading" before you open OneTask on the second computer.
