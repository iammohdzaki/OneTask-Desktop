package com.one.task.presentation.ui.components.blocks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.one.task.domain.LinkBlock
import com.one.task.presentation.ui.Dimens
import com.one.task.presentation.ui.Motion

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LinkBlockEditor(block: LinkBlock, onUpdate: (LinkBlock) -> Unit) {
    var isEditing by remember { mutableStateOf(block.url.isBlank()) }
    var localUrl by remember(block.id) { mutableStateOf(block.url) }
    var localLabel by remember(block.id) { mutableStateOf(block.label) }
    var isHovered by remember { mutableStateOf(false) }

    val uriHandler = LocalUriHandler.current

    // Auto-detect link type from URL
    val detectedType = remember(localUrl) {
        when {
            localUrl.contains("github.com", ignoreCase = true) -> "github"
            else -> "generic"
        }
    }

    val linkIcon: ImageVector = Icons.Default.Link

    val linkColor = when (detectedType) {
        "github" -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.primary
    }

    val containerColor by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colorScheme.surfaceContainerHigh
        else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = Motion.Spec.standard()
    )

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.01f else 1.0f,
        animationSpec = Motion.Spec.springBouncy()
    )

    if (isEditing) {
        // Edit mode: URL + label inputs
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(
                    Dimens.spaceBorder,
                    MaterialTheme.colorScheme.outlineVariant,
                    MaterialTheme.shapes.medium
                )
                .padding(Dimens.spaceM),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceXS)
        ) {
            Text(
                "Link Block",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // URL field
            BasicTextField(
                value = localUrl,
                onValueChange = { localUrl = it },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(Dimens.spaceXS),
                decorationBox = { inner ->
                    if (localUrl.isEmpty()) {
                        Text(
                            "https://...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                    inner()
                }
            )

            // Label field
            BasicTextField(
                value = localLabel,
                onValueChange = { localLabel = it },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(Dimens.spaceXS),
                decorationBox = { inner ->
                    if (localLabel.isEmpty()) {
                        Text(
                            "Display label (optional)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                    inner()
                }
            )

            // Save button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                FilledTonalButton(
                    onClick = {
                        val finalLabel = localLabel.ifBlank { localUrl }
                        val finalType = when {
                            localUrl.contains("github.com", ignoreCase = true) -> "github"
                            else -> "generic"
                        }
                        onUpdate(block.copy(url = localUrl, label = finalLabel, linkType = finalType))
                        isEditing = false
                    },
                    contentPadding = PaddingValues(horizontal = Dimens.spaceM, vertical = Dimens.spaceXXS)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(Dimens.iconSmall))
                    Spacer(Modifier.width(Dimens.spaceXXS))
                    Text("Save", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    } else {
        // Display mode: clickable link card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .clip(MaterialTheme.shapes.medium)
                .background(containerColor)
                .border(
                    Dimens.spaceBorder,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    MaterialTheme.shapes.medium
                )
                .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                .onPointerEvent(PointerEventType.Exit) { isHovered = false }
                .clickable {
                    if (block.url.isNotBlank()) {
                        try { uriHandler.openUri(block.url) } catch (_: Exception) {}
                    }
                }
                .padding(horizontal = Dimens.spaceM, vertical = Dimens.spaceS),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceS)
        ) {
            // Link type icon / badge
            Box(
                modifier = Modifier
                    .size(Dimens.iconLarge)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        if (detectedType == "github") MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (detectedType == "github") {
                    // GitHub mark using text (G)
                    Text(
                        "G",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Icon(
                        linkIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.iconMedium)
                    )
                }
            }

            // Labels
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = block.label.ifBlank { block.url },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = linkColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (block.label.isNotBlank()) {
                    Text(
                        text = block.url,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Edit button visible on hover
            if (isHovered) {
                IconButton(
                    onClick = { isEditing = true },
                    modifier = Modifier.size(Dimens.iconLarge)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit link",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dimens.iconMedium)
                    )
                }
            }
        }
    }
}
