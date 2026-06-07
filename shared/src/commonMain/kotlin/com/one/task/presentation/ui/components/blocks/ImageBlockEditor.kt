package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.one.task.domain.ImageBlock
import com.one.task.domain.pickImageFile
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import onetask.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import okio.Path.Companion.toPath
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import com.one.task.domain.loadLocalImage

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImageBlockEditor(block: ImageBlock, onUpdate: (ImageBlock) -> Unit) {
    var localCaption by remember(block.id) { mutableStateOf(block.caption) }
    var localSubtitle by remember(block.id) { mutableStateOf(block.subtitle) }
    var localUrl by remember(block.id) { mutableStateOf(block.url ?: "") }
    var isEditing by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    val imageHeight = when (block.sizeMode) {
        "Small" -> 200.dp
        "Medium" -> 400.dp
        "Large" -> 600.dp
        else -> 400.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false }
    ) {
        if (isEditing) {
            // Editor Toolbar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(Res.string.image_size_label), style = MaterialTheme.typography.labelMedium)
                        listOf("Small", "Medium", "Large").forEach { size ->
                            val displaySize = when(size) {
                                "Small" -> stringResource(Res.string.image_size_small)
                                "Large" -> stringResource(Res.string.image_size_large)
                                else -> stringResource(Res.string.image_size_medium)
                            }
                            FilterChip(
                                selected = block.sizeMode == size,
                                onClick = { onUpdate(block.copy(sizeMode = size)) },
                                label = { Text(displaySize) }
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = block.showCaption,
                                onCheckedChange = { onUpdate(block.copy(showCaption = it)) }
                            )
                            Text(stringResource(Res.string.image_caption_label), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    
                    IconButton(onClick = { isEditing = false }) {
                        Icon(Icons.Default.Check, contentDescription = stringResource(Res.string.content_desc_done_editing), tint = MaterialTheme.colorScheme.primary)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = localUrl,
                    onValueChange = {
                        localUrl = it
                        onUpdate(block.copy(url = it.takeIf { it.isNotBlank() }))
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.small)
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        if (localUrl.isEmpty()) {
                            Text("Image URL (optional)", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)))
                        }
                        innerTextField()
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .clickable(enabled = isEditing) {
                    val path = pickImageFile()
                    if (path != null) {
                        onUpdate(block.copy(localPath = "file://$path"))
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (!block.url.isNullOrBlank()) {
                CoilImage(
                    imageModel = { block.url },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            } else if (block.localPath.isNotBlank()) {
                var pathStr = block.localPath.removePrefix("file://")
                if (pathStr.startsWith("/") && pathStr.drop(1).contains(":")) {
                    pathStr = pathStr.removePrefix("/")
                }
                
                var bitmap by remember(pathStr) { mutableStateOf<ImageBitmap?>(null) }
                var loadFailed by remember(pathStr) { mutableStateOf(false) }

                LaunchedEffect(pathStr) {
                    println("ImageBlockEditor: Attempting to load image.")
                    println("ImageBlockEditor: Original block.localPath = ${block.localPath}")
                    println("ImageBlockEditor: Cleaned pathStr = $pathStr")
                    withContext(Dispatchers.IO) {
                        try {
                            val loaded = loadLocalImage(pathStr)
                            if (loaded != null) {
                                println("ImageBlockEditor: Successfully loaded image bitmap.")
                                bitmap = loaded
                            } else {
                                println("ImageBlockEditor: loadLocalImage returned null.")
                                loadFailed = true
                            }
                        } catch (e: Exception) {
                            println("ImageBlockEditor: Exception while loading image: ${e.message}")
                            e.printStackTrace()
                            loadFailed = true
                        }
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!,
                        contentDescription = block.caption,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (loadFailed) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.BrokenImage,
                            contentDescription = "Image not found",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Image file moved or deleted", 
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    }
                } else {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
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
            
            if (!isEditing && isHovered) {
                IconButton(
                    onClick = { isEditing = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), shape = MaterialTheme.shapes.small)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Image")
                }
            }
        }

        if (block.showCaption) {
            if (isEditing) {
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
                                    stringResource(Res.string.hint_add_caption),
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
                                    stringResource(Res.string.hint_add_subtitle),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            } else if (localCaption.isNotBlank() || localSubtitle.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (localCaption.isNotBlank()) {
                        Text(
                            text = localCaption,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    if (localSubtitle.isNotBlank()) {
                        Text(
                            text = localSubtitle,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}
