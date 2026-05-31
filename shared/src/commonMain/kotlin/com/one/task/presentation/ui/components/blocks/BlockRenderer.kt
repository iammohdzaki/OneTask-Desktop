package com.one.task.presentation.ui.components.blocks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.one.task.domain.ContentBlock
import com.one.task.domain.TextBlock
import com.one.task.domain.CheckboxBlock
import com.one.task.domain.ImageBlock
import com.one.task.domain.TableBlock

@Composable
fun BlockRenderer(block: ContentBlock, onUpdate: (ContentBlock) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        when (block) {
            is TextBlock -> TextBlockEditor(block) { onUpdate(it) }
            is CheckboxBlock -> CheckboxBlockEditor(block) { onUpdate(it) }
            is ImageBlock -> ImageBlockEditor(block) { onUpdate(it) }
            is TableBlock -> TableBlockEditor(block) { onUpdate(it) }
        }
    }
}
