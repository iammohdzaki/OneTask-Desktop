package com.one.task.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.one.task.CustomVerticalScrollbar
import com.one.task.domain.ContentBlock
import com.one.task.presentation.ui.components.blocks.BlockRenderer

@Composable
fun MainEditorCanvas(pageTitle: String, blocks: List<ContentBlock>, onUpdateBlock: (ContentBlock) -> Unit) {
    val listState = rememberLazyListState()
    
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                        .padding(horizontal = 32.dp, vertical = 48.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = pageTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha=0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("#high-priority", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondary.copy(alpha=0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("#q3-launch", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    blocks.forEach { block ->
                        BlockRenderer(
                            block = block,
                            onUpdate = onUpdateBlock
                        )
                    }
                }
            }
        }
        
        CustomVerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(end = 4.dp),
            state = listState
        )
    }
}
