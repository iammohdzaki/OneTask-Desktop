package com.one.task.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.one.task.domain.Notebook
import com.one.task.domain.loadLocalImage
import com.one.task.presentation.ui.utils.IconHelper
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RailSidebar(
    notebooks: List<Notebook>,
    activeNotebookId: String?,
    onCreateNotebookClick: () -> Unit,
    onSelectNotebook: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.launcher),
                contentDescription = stringResource(Res.string.app_name),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.width(32.dp).height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Notebooks
        notebooks.forEach { notebook ->
            val isSelected = notebook.id == activeNotebookId
            var isHovered by remember { mutableStateOf(false) }

            val iconColor = if (notebook.colorHex != null) Color(
                notebook.colorHex.removePrefix("#").toLong(16) or 0xFF000000
            ) else MaterialTheme.colorScheme.primary

            // Animations
            val animatedScale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else if (isHovered) 1.05f else 1.0f,
                animationSpec = tween(durationMillis = 200)
            )

            val animatedBackground by animateColorAsState(
                targetValue = if (isSelected) iconColor.copy(alpha = 0.15f)
                else if (isHovered) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                else Color.Transparent,
                animationSpec = tween(durationMillis = 200)
            )

            val iconTint by animateColorAsState(
                targetValue = if (isSelected || isHovered) iconColor else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = 200)
            )

            val indicatorHeight by animateDpAsState(
                targetValue = if (isSelected) 24.dp else if (isHovered) 8.dp else 0.dp,
                animationSpec = tween(durationMillis = 200)
            )

            val iconVector = IconHelper.getNotebookIcon(notebook.iconName)

            Box(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                // Left Indicator Line
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 2.dp)
                        .width(4.dp)
                        .height(indicatorHeight)
                        .clip(RoundedCornerShape(2.dp))
                        .background(iconColor)
                )

                // Icon Container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(animatedScale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(animatedBackground)
                        .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                        .onPointerEvent(PointerEventType.Exit) { isHovered = false }
                        .clickable { onSelectNotebook(notebook.id) },
                    contentAlignment = Alignment.Center
                ) {
                    if (notebook.iconUrl != null) {
                        val bitmap = remember(notebook.iconUrl) { loadLocalImage(notebook.iconUrl) }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = notebook.name,
                                modifier = Modifier.size(24.dp).clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Icon(
                                iconVector,
                                contentDescription = notebook.name,
                                tint = iconTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            iconVector,
                            contentDescription = notebook.name,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        RailIcon(
            Icons.Default.Add,
            stringResource(Res.string.add_notebook),
            modifier = Modifier.padding(bottom = 8.dp),
            onClick = {
                onCreateNotebookClick()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        RailIcon(
            Icons.Outlined.Settings,
            stringResource(Res.string.content_desc_settings),
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = onSettingsClick
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RailIcon(
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    val animatedBackground by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f) else background,
        animationSpec = tween(durationMillis = 200)
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isHovered) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .scale(animatedScale)
            .clip(RoundedCornerShape(12.dp))
            .background(animatedBackground)
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false }
            .clickable { onClick.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = description, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
