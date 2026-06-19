package com.one.task.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.one.task.presentation.ui.Motion
import com.one.task.presentation.ui.Dimens
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
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
            .width(Dimens.railWidth)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(top = Dimens.spaceM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon
        Box(
            modifier = Modifier
                .size(Dimens.touchTarget)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.launcher),
                contentDescription = stringResource(Res.string.app_name),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(Dimens.iconNormal)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spaceM))
        Box(
            modifier = Modifier.width(Dimens.spaceXXL).height(Dimens.spaceBorder)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        )
        Spacer(modifier = Modifier.height(Dimens.spaceM))

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
                animationSpec = Motion.Spec.springStandard()
            )

            val animatedBackground by animateColorAsState(
                targetValue = if (isSelected) iconColor.copy(alpha = 0.15f)
                else if (isHovered) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                else Color.Transparent,
                animationSpec = Motion.Spec.standard()
            )

            val iconTint by animateColorAsState(
                targetValue = if (isSelected || isHovered) iconColor else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = Motion.Spec.standard()
            )

            val indicatorHeight by animateDpAsState(
                targetValue = if (isSelected) Dimens.spaceXL else if (isHovered) Dimens.spaceXS else 0.dp,
                animationSpec = Motion.Spec.springStiff()
            )

            val iconVector = IconHelper.getNotebookIcon(notebook.iconName)

            Box(
                modifier = Modifier
                    .padding(bottom = Dimens.spaceS)
                    .fillMaxWidth()
                    .height(Dimens.touchTarget),
                contentAlignment = Alignment.Center
            ) {
                // Left Indicator Line
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 2.dp)
                        .width(Dimens.spaceXXS)
                        .height(indicatorHeight)
                        .clip(RoundedCornerShape(2.dp))
                        .background(iconColor)
                )

                // Icon Container
                Box(
                    modifier = Modifier
                        .size(Dimens.touchTarget)
                        .scale(animatedScale)
                        .clip(MaterialTheme.shapes.medium)
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
                                modifier = Modifier.size(Dimens.iconNormal).clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Icon(
                                iconVector,
                                contentDescription = notebook.name,
                                tint = iconTint,
                                modifier = Modifier.size(Dimens.iconNormal)
                            )
                        }
                    } else {
                        Icon(
                            iconVector,
                            contentDescription = notebook.name,
                            tint = iconTint,
                            modifier = Modifier.size(Dimens.iconNormal)
                        )
                    }

                    // Hover tooltip showing notebook name
                    if (isHovered) {
                        val density = LocalDensity.current
                        // CenterStart anchors the popup's LEFT edge at the parent's left edge,
                        // centered vertically. Shifting right by touchTarget+gap places it
                        // just past the icon's right edge with a small breathing gap.
                        val xOffsetPx = with(density) { (Dimens.touchTarget + Dimens.spaceXXS).roundToPx() }
                        Popup(
                            alignment = Alignment.CenterStart,
                            offset = androidx.compose.ui.unit.IntOffset(x = xOffsetPx, y = 0),
                            properties = PopupProperties(focusable = false)
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.inverseSurface,
                                tonalElevation = 4.dp,
                                shadowElevation = 4.dp
                            ) {
                                Text(
                                    text = notebook.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.inverseOnSurface,
                                    modifier = Modifier.padding(
                                        horizontal = Dimens.spaceXS,
                                        vertical = Dimens.spaceXXS
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        RailIcon(
            Icons.Default.Add,
            stringResource(Res.string.add_notebook),
            modifier = Modifier.padding(bottom = Dimens.spaceXS),
            onClick = {
                onCreateNotebookClick()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        RailIcon(
            Icons.Outlined.Settings,
            stringResource(Res.string.content_desc_settings),
            modifier = Modifier.padding(bottom = Dimens.spaceM),
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
        animationSpec = Motion.Spec.standard()
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isHovered) 1.1f else 1.0f,
        animationSpec = Motion.Spec.springBouncy()
    )

    Box(
        modifier = modifier
            .size(Dimens.touchTarget)
            .scale(animatedScale)
            .clip(MaterialTheme.shapes.medium)
            .background(animatedBackground)
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false }
            .clickable { onClick.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = description, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
