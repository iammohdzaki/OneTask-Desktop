package com.one.task

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CustomVerticalScrollbar(
    modifier: Modifier,
    state: LazyListState
)
