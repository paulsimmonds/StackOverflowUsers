package com.magictorch.stackoverflowtest.presentation.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ImageLoader {
    @Composable
    fun Load(
        model: Any?,
        contentDescription: String?,
        modifier: Modifier
    )
}
