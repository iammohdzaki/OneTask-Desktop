package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.one.task.domain.ImageBlock
import com.one.task.domain.loadLocalImage
import com.one.task.domain.pickImageFile
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ImageBlockEditor(block: ImageBlock, onUpdate: (ImageBlock) -> Unit) {
    var localCaption by remember(block.id) { mutableStateOf(block.caption) }
    var localSubtitle by remember(block.id) { mutableStateOf(block.subtitle) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
        val bitmap = remember(block.localPath) {
            if (block.localPath.isNotBlank()) loadLocalImage(block.localPath) else null
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (bitmap != null) 400.dp else 200.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .clickable {
                    val path = pickImageFile()
                    if (path != null) {
                        onUpdate(block.copy(localPath = path))
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = block.caption,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        stringResource(Res.string.image_prefix), 
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp)
        ) {
            BasicTextField(
                value = localCaption,
                onValueChange = {
                    localCaption = it
                    onUpdate(block.copy(caption = it))
                },
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (localCaption.isEmpty()) {
                        Text(
                            "Add a caption...",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    innerTextField()
                }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            BasicTextField(
                value = localSubtitle,
                onValueChange = {
                    localSubtitle = it
                    onUpdate(block.copy(subtitle = it))
                },
                textStyle = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (localSubtitle.isEmpty()) {
                        Text(
                            "Add a subtitle...",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}
