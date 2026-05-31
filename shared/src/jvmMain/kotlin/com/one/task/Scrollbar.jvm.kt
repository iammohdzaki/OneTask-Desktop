package com.one.task

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
actual fun CustomVerticalScrollbar(
    modifier: Modifier,
    state: LazyListState
) {
    VerticalScrollbar(
        modifier = modifier,
        adapter = rememberScrollbarAdapter(state),
        style = LocalScrollbarStyle.current.copy(
            unhoverColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
            hoverColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f)
        )
    )
}
