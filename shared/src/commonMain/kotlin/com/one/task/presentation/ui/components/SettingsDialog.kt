package com.one.task.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.one.task.presentation.mvi.SettingsIntent
import com.one.task.presentation.mvi.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsDialog(
    onDismissRequest: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedCategory by remember { mutableStateOf("Appearance") }

    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .pointerInput(Unit) { detectTapGestures(onTap = { onDismissRequest() }) },
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .width(700.dp)
                    .height(500.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .pointerInput(Unit) { detectTapGestures(onTap = {}) }
            ) {
                // Sidebar
                Column(
                    modifier = Modifier
                        .width(220.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(16.dp)
                ) {
                    Text("Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    CategoryItem("Appearance", Icons.Default.ColorLens, selectedCategory == "Appearance") { selectedCategory = "Appearance" }
                    Spacer(modifier = Modifier.height(4.dp))
                    //CategoryItem("Editor", Icons.Default.EditNote, selectedCategory == "Editor") { selectedCategory = "Editor" }
                    //Spacer(modifier = Modifier.height(4.dp))
                    //CategoryItem("Security", Icons.Default.Lock, selectedCategory == "Security") { selectedCategory = "Security" }
                    //Spacer(modifier = Modifier.height(4.dp))
                    CategoryItem("Data", Icons.Default.Storage, selectedCategory == "Data") { selectedCategory = "Data" }
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(24.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedCategory, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(24.dp))

                    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                        when (selectedCategory) {
                            "Appearance" -> {
                                Text("Theme", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ThemeOption("System", state.themeMode == "System") { viewModel.onIntent(SettingsIntent.SetThemeMode("System")) }
                                    ThemeOption("Light", state.themeMode == "Light") { viewModel.onIntent(SettingsIntent.SetThemeMode("Light")) }
                                    ThemeOption("Dark", state.themeMode == "Dark") { viewModel.onIntent(SettingsIntent.SetThemeMode("Dark")) }
                                }
                                
                                Spacer(modifier = Modifier.height(32.dp))
                                
                                Text("Typography", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Font Size", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        IconButton(onClick = { viewModel.onIntent(SettingsIntent.SetFontSize(state.fontSize - 1)) }, enabled = state.fontSize > 12) {
                                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
                                        }
                                        Text("${state.fontSize}px", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        IconButton(onClick = { viewModel.onIntent(SettingsIntent.SetFontSize(state.fontSize + 1)) }, enabled = state.fontSize < 32) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                            "Editor" -> {
                                SettingsSwitch(
                                    title = "Full Width Editor",
                                    subtitle = "Expand editor to occupy full window width",
                                    checked = state.fullWidthEditor,
                                    onCheckedChange = { viewModel.onIntent(SettingsIntent.SetFullWidthEditor(it)) }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                SettingsSwitch(
                                    title = "Show Line Numbers",
                                    subtitle = "Display line numbers in text blocks",
                                    checked = state.showLineNumbers,
                                    onCheckedChange = { viewModel.onIntent(SettingsIntent.SetShowLineNumbers(it)) }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                SettingsSwitch(
                                    title = "Auto Save",
                                    subtitle = "Automatically save changes as you type",
                                    checked = state.autoSave,
                                    onCheckedChange = { viewModel.onIntent(SettingsIntent.SetAutoSave(it)) }
                                )
                            }
                            "Security" -> {
                                SettingsSwitch(
                                    title = "Password Authentication",
                                    subtitle = "Require a password to open the app",
                                    checked = state.passwordAuthEnabled,
                                    onCheckedChange = { viewModel.onIntent(SettingsIntent.SetPasswordAuthEnabled(it)) }
                                )
                            }
                            "Data" -> {
                                 val filePicker = com.one.task.presentation.ui.utils.getFilePicker()
                                 var showClearConfirm by remember { mutableStateOf(false) }
                                 var showRestartDialog by remember { mutableStateOf(false) }

                                 Text("Storage Management", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                 Spacer(modifier = Modifier.height(16.dp))
                                 
                                 // Database Location
                                 Surface(
                                     onClick = {
                                         filePicker.pickFolder { path ->
                                             viewModel.onIntent(SettingsIntent.SetDatabasePath(path))
                                             showRestartDialog = true
                                         }
                                     },
                                     shape = RoundedCornerShape(8.dp),
                                     color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                     modifier = Modifier.fillMaxWidth()
                                 ) {
                                     Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                         Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                         Spacer(modifier = Modifier.width(16.dp))
                                         Column {
                                             Text("Database Location", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                             Text(state.databasePath ?: "Default (AppData/OneTask)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                         }
                                     }
                                 }
                                 
                                 Spacer(modifier = Modifier.height(12.dp))

                                 DataActionItem("Export Data", "Download your workspace as a JSON file", Icons.Default.Download) { 
                                     viewModel.onIntent(SettingsIntent.ExportData { json ->
                                         filePicker.saveFile("onetask_backup", json, "json")
                                     })
                                 }
                                 Spacer(modifier = Modifier.height(12.dp))
                                 DataActionItem("Import Data", "Restore your workspace from a backup file", Icons.Default.Upload) { 
                                     filePicker.pickFile("json") { json ->
                                         viewModel.onIntent(SettingsIntent.ImportData(json) {
                                             onDismissRequest() // Close settings on success
                                         })
                                     }
                                 }
                                 Spacer(modifier = Modifier.height(24.dp))
                                 DangerActionItem("Clear All Data", "Permanently delete all notebooks, pages, and blocks", Icons.Default.DeleteForever) { 
                                     showClearConfirm = true
                                 }
                                 
                                 if (showClearConfirm) {
                                     AlertDialog(
                                         onDismissRequest = { showClearConfirm = false },
                                         title = { Text("Clear All Data?") },
                                         text = { Text("This will permanently delete all your notebooks, pages, and content. This action cannot be undone.") },
                                         confirmButton = {
                                             TextButton(
                                                 onClick = {
                                                     viewModel.onIntent(SettingsIntent.ClearAllData)
                                                     showClearConfirm = false
                                                 },
                                                 colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                             ) {
                                                 Text("Clear Everything")
                                             }
                                         },
                                         dismissButton = {
                                             TextButton(onClick = { showClearConfirm = false }) {
                                                 Text("Cancel")
                                             }
                                         }
                                     )
                                 }

                                 if (showRestartDialog) {
                                     AlertDialog(
                                         onDismissRequest = { showRestartDialog = false },
                                         title = { Text("Restart Required") },
                                         text = { Text("Changing the database location requires an application restart to take effect. Please restart the app manually.") },
                                         confirmButton = {
                                             TextButton(onClick = { showRestartDialog = false }) {
                                                 Text("I Understand")
                                             }
                                         }
                                     )
                                 }
                             }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitch(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun DataActionItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DangerActionItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun CategoryItem(name: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal))
    }
}

@Composable
private fun ThemeOption(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(name, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
    }
}
