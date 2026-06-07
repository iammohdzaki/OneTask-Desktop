package com.one.task.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.one.task.presentation.ui.Dimens

@Composable
fun TopAppBar(
    title: String,
    isSaving: Boolean,
    showMenuIcon: Boolean = false,
    isSidebarCollapsed: Boolean = false,
    onSidebarToggle: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.topBarHeight)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = Dimens.spaceL),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXS)) {
            if (showMenuIcon) {
                Box(
                    modifier = Modifier.size(Dimens.touchTarget)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.content_desc_back),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (onSidebarToggle != null) {
                Box(
                    modifier = Modifier
                        .size(Dimens.touchTarget)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onSidebarToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSidebarCollapsed) Icons.Default.Menu else Icons.AutoMirrored.Filled.MenuOpen,
                        contentDescription = "Toggle Sidebar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.spaceXXS))
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(horizontal = Dimens.spaceXS, vertical = Dimens.spaceXXS)
            ) {
                Text(stringResource(Res.string.topbar_editor), style = MaterialTheme.typography.labelMedium)
            }
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXS)) {
            val containerColor = if (isSaving) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer
            val contentColor = if (isSaving) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(containerColor)
                    .padding(horizontal = Dimens.spaceXS, vertical = Dimens.spaceXXS),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSaving) stringResource(Res.string.status_saving) else stringResource(Res.string.status_saved),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor
                )
            }
        }
    }
}
