package com.one.task.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.one.task.presentation.mvi.AppIntent
import com.one.task.presentation.mvi.AppViewModel
import com.one.task.presentation.ui.components.MainEditorCanvas
import com.one.task.presentation.ui.components.PagesSidebar
import com.one.task.presentation.ui.components.RailSidebar
import com.one.task.presentation.ui.components.TopAppBar
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.one.task.presentation.ui.components.CreateNotebookDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Icon

@Composable
fun WorkspaceScreen(viewModel: AppViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    var showCreateNotebookDialog by remember { mutableStateOf(false) }

    if (showCreateNotebookDialog) {
        CreateNotebookDialog(
            onDismissRequest = { showCreateNotebookDialog = false },
            onCreate = { name, iconName, colorHex, isPrivate ->
                viewModel.onIntent(AppIntent.CreateNotebook(name, iconName, colorHex, isPrivate))
                showCreateNotebookDialog = false
            }
        )
    }
    
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = maxWidth
        val isCompact = width < 600.dp
        val isExpanded = width >= 840.dp
        
        Row(modifier = Modifier.fillMaxSize()) {
            if (!isCompact) {
                RailSidebar(
                    notebooks = state.notebooks,
                    activeNotebookId = state.activeNotebookId,
                    onCreateNotebookClick = { showCreateNotebookDialog = true },
                    onSelectNotebook = { viewModel.onIntent(AppIntent.SelectNotebook(it)) }
                )
            }
            
            if (isExpanded) {
                PagesSidebar(
                    pages = state.pagesForActiveNotebook,
                    selectedPageId = state.activePageId,
                    onSelect = { viewModel.onIntent(AppIntent.SelectPage(it.id)) },
                    onCreatePage = {
                        state.activeNotebookId?.let { nbId ->
                            viewModel.onIntent(AppIntent.CreatePage(nbId, "New Page"))
                        }
                    }
                )
            }
            
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                val activePage = state.pagesForActiveNotebook.find { it.id == state.activePageId }
                TopAppBar(
                    title = activePage?.title ?: stringResource(Res.string.empty_page_title),
                    showMenuIcon = !isExpanded
                )
                
                if (state.activeNotebookId == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Create a Notebook to get started", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else if (activePage != null) {
                    MainEditorCanvas(
                        pageTitle = activePage.title,
                        blocks = state.activePageBlocks,
                        onUpdateBlock = { viewModel.onIntent(AppIntent.UpdateBlock(it)) }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(Res.string.empty_page_message),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
