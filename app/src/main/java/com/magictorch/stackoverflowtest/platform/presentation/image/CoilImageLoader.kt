package com.magictorch.stackoverflowtest.platform.presentation.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.magictorch.stackoverflowtest.presentation.image.ImageLoader

class CoilImageLoader : ImageLoader {
    @Composable
    override fun Load(
        model: Any?,
        contentDescription: String?,
        modifier: Modifier
    ) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}
