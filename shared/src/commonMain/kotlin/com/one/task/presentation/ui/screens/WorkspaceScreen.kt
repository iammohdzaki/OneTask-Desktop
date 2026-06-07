package com.one.task.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.one.task.presentation.ui.Motion
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.one.task.presentation.ui.Dimens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.one.task.presentation.mvi.AppIntent
import com.one.task.presentation.mvi.AppViewModel
import com.one.task.presentation.ui.components.*
import onetask.shared.generated.resources.Res
import onetask.shared.generated.resources.empty_notebook_prompt
import onetask.shared.generated.resources.empty_page_message
import onetask.shared.generated.resources.empty_page_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun WorkspaceScreen(viewModel: AppViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    var showCreateNotebookDialog by remember { mutableStateOf(false) }
    var showCreatePageDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }
    var isSidebarCollapsed by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(onDismissRequest = { showSettingsDialog = false })
    }

    if (showArchiveDialog) {
        ArchiveDialog(
            archivedPages = state.archivedPages,
            onDismissRequest = { showArchiveDialog = false },
            onRestore = { viewModel.onIntent(AppIntent.RestorePage(it)) },
            onDelete = { viewModel.onIntent(AppIntent.DeletePage(it)) },
            onEmptyArchive = { viewModel.onIntent(AppIntent.EmptyArchive) }
        )
    }

    if (showCreateNotebookDialog) {
        CreateNotebookDialog(
            onDismissRequest = { showCreateNotebookDialog = false },
            onCreate = { name, iconName, colorHex, isPrivate, iconUrl ->
                viewModel.onIntent(AppIntent.CreateNotebook(name, iconName, colorHex, isPrivate, iconUrl))
                showCreateNotebookDialog = false
            }
        )
    }

    if (showCreatePageDialog) {
        CreatePageDialog(
            onDismissRequest = { showCreatePageDialog = false },
            onCreate = { title, description, iconName ->
                state.activeNotebookId?.let { nbId ->
                    viewModel.onIntent(AppIntent.CreatePage(nbId, title, description, iconName))
                }
                showCreatePageDialog = false
            }
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val windowSizeClass = calculateWindowSizeClass()
        val widthSizeClass = windowSizeClass.widthSizeClass
        val isCompact = widthSizeClass == WindowWidthSizeClass.Compact
        val isExpanded = widthSizeClass == WindowWidthSizeClass.Expanded

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Row(modifier = Modifier.fillMaxSize()) {
                if (!isCompact) {
                    RailSidebar(
                        notebooks = state.notebooks,
                        activeNotebookId = state.activeNotebookId,
                        onCreateNotebookClick = { showCreateNotebookDialog = true },
                        onSelectNotebook = { viewModel.onIntent(AppIntent.SelectNotebook(it)) },
                        onSettingsClick = { showSettingsDialog = true }
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded && !isSidebarCollapsed,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it / 2 },
                        animationSpec = Motion.Spec.enter()
                    ) + fadeIn(animationSpec = Motion.Spec.enter()),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it / 2 },
                        animationSpec = Motion.Spec.exit()
                    ) + fadeOut(animationSpec = Motion.Spec.exit())
                ) {
                    PagesSidebar(
                        pages = state.pagesForActiveNotebook,
                        selectedPageId = state.activePageId,
                        onSelect = { viewModel.onIntent(AppIntent.SelectPage(it.id)) },
                        onCreatePage = { showCreatePageDialog = true },
                        onArchivePage = { viewModel.onIntent(AppIntent.ArchivePage(it)) },
                        onOpenArchive = { showArchiveDialog = true }
                    )
                }

                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    val activePage = state.pagesForActiveNotebook.find { it.id == state.activePageId }
                    TopAppBar(
                        title = activePage?.title ?: stringResource(Res.string.empty_page_title),
                        isSaving = state.isSaving,
                        showMenuIcon = !isExpanded,
                        isSidebarCollapsed = isSidebarCollapsed,
                        onSidebarToggle = if (isExpanded) { { isSidebarCollapsed = !isSidebarCollapsed } } else null
                    )

                    if (state.activeNotebookId == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.AutoMirrored.Filled.LibraryBooks,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(Dimens.iconExtraExtraLarge)
                                )
                                Spacer(modifier = Modifier.height(Dimens.spaceM))
                                Text(
                                    stringResource(Res.string.empty_notebook_prompt),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else if (activePage != null) {
                        MainEditorCanvas(
                            pageId = activePage.id,
                            pageTitle = activePage.title,
                            pageDescription = activePage.description,
                            tags = activePage.tags,
                            blocks = state.activePageBlocks,
                            onTitleChange = { newTitle ->
                                viewModel.onIntent(AppIntent.RenamePageTitle(activePage.id, newTitle))
                            },
                            onDescriptionChange = { newDesc ->
                                viewModel.onIntent(AppIntent.RenamePageDescription(activePage.id, newDesc))
                            },
                            onAddTag = { tag ->
                                viewModel.onIntent(AppIntent.AddTag(activePage.id, tag))
                            },
                            onRemoveTag = { tag ->
                                viewModel.onIntent(AppIntent.RemoveTag(activePage.id, tag))
                            },
                            onUpdateBlock = { viewModel.onIntent(AppIntent.UpdateBlock(it)) },
                            onAddBlock = { block ->
                                viewModel.onIntent(AppIntent.AddBlock(activePage.id, block))
                            },
                            onDeleteBlock = { blockId ->
                                viewModel.onIntent(AppIntent.DeleteBlock(activePage.id, blockId))
                            },
                            onReorderBlocks = { reordered ->
                                viewModel.onIntent(AppIntent.ReorderBlocks(activePage.id, reordered))
                            }
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
}
