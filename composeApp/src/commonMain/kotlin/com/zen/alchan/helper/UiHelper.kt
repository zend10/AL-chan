package com.zen.alchan.helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Composable
expect fun getScreenWidth(): Dp

@Composable
expect fun getScreenHeight(): Dp

@Composable
fun isWideScreen(): Boolean {
    return getScreenWidth() >= getScreenHeight()
}