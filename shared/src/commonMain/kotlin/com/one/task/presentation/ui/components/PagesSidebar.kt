package com.one.task.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import com.one.task.presentation.ui.Motion
import com.one.task.presentation.ui.Dimens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.one.task.domain.Page
import com.one.task.presentation.ui.utils.IconHelper
import onetask.shared.generated.resources.Res
import onetask.shared.generated.resources.create
import onetask.shared.generated.resources.sidebar_archive
import onetask.shared.generated.resources.sidebar_pages
import org.jetbrains.compose.resources.stringResource

@Composable
fun PagesSidebar(
    pages: List<Page>,
    selectedPageId: String?,
    onSelect: (Page) -> Unit,
    onCreatePage: () -> Unit,
    onArchivePage: (String) -> Unit,
    onOpenArchive: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(Dimens.sidebarWidth)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(vertical = Dimens.spaceM)
    ) {
        Column(modifier = Modifier.padding(start = Dimens.spaceL, end = Dimens.spaceS, bottom = Dimens.spaceXXS)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(Res.string.sidebar_pages),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Box(
                    modifier = Modifier
                        .size(Dimens.iconLarge)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onCreatePage() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Page",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.iconMedium)
                    )
                }
            }
            Text(
                "Last edited 2m ago",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spaceM))

        if (pages.isEmpty()) {
            val infiniteTransition = rememberInfiniteTransition()
            val floatOffset by infiniteTransition.animateFloat(
                initialValue = -5f,
                targetValue = 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = Dimens.spaceXL),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(Dimens.railWidth)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.EditNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(Dimens.iconExtraLarge)
                                .offset(y = floatOffset.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.spaceL))
                    Text(
                        text = "It's quiet here...",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceXS))
                    Text(
                        text = "Create a page to start capturing your thoughts and ideas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceXL))
                    Button(
                        onClick = onCreatePage,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(Dimens.iconMedium)
                        )
                        Spacer(modifier = Modifier.width(Dimens.spaceXS))
                        Text(stringResource(Res.string.create), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(pages, key = { it.id }) { page ->
                    PageRow(
                        page = page,
                        isSelected = page.id == selectedPageId,
                        onSelect = { onSelect(page) },
                        onArchive = { onArchivePage(page.id) }
                    )
                }
            }
        }

        // Bottom Section
        Spacer(modifier = Modifier.height(Dimens.spaceM))
        Column(modifier = Modifier.padding(horizontal = Dimens.spaceXS)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onOpenArchive() }
                    .padding(horizontal = Dimens.spaceS, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Archive,
                    contentDescription = stringResource(Res.string.sidebar_archive),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.iconMedium)
                )
                Spacer(modifier = Modifier.width(Dimens.spaceS))
                Text(
                    text = stringResource(Res.string.sidebar_archive),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PageRow(
    page: Page,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onArchive: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    val animatedBackground by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f) 
                      else if (isHovered) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                      else Color.Transparent,
        animationSpec = Motion.Spec.standard()
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isHovered && !isSelected) 1.02f else 1.0f,
        animationSpec = Motion.Spec.springBouncy()
    )

    Box(modifier = Modifier.scale(animatedScale)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(animatedBackground)
                .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                .onPointerEvent(PointerEventType.Exit) { isHovered = false }
                .clickable { onSelect() }
                .pointerInput(page.id) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Press &&
                                event.button == PointerButton.Secondary
                            ) {
                                showMenu = true
                            }
                        }
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicator line
            Box(
                modifier = Modifier
                    .width(Dimens.spaceIndicator)
                    .fillMaxHeight()
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.spaceM, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = IconHelper.getIcon(page.iconName),
                    contentDescription = page.iconName,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.iconMedium)
                )
                Spacer(modifier = Modifier.width(Dimens.spaceS))
                Column {
                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!page.description.isNullOrBlank()) {
                        Text(
                            text = page.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
                text = { Text("Archive", color = MaterialTheme.colorScheme.onSurface) },
                onClick = {
                    onArchive()
                    showMenu = false
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Archive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }
}
