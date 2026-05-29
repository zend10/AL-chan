package com.zen.alchan.helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

@Composable
actual fun getScreenWidth(): Dp {
    return LocalWindowInfo.current.containerDpSize.width
}

@Composable
actual fun getScreenHeight(): Dp {
    return LocalWindowInfo.current.containerDpSize.height
}