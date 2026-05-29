package com.zen.alchan.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.zen.alchan.DefaultTheme

@Composable
fun AvatarImage(
    imageUrl: String,
    size: Dp = DefaultTheme.dimen.avatarSize,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    DefaultImage(
        imageUrl = imageUrl,
        contentDescription = null,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onBackground)
            .border(
                BorderStroke(
                    DefaultTheme.dimen.lineWidth,
                    MaterialTheme.colorScheme.onBackground
                ),
                CircleShape
            )
            .size(size)
            .clickable(onClick = onClick)
    )
}