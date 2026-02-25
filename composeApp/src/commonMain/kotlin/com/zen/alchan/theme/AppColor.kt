package com.zen.alchan.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object AppColor {
    val Black = Color(0xFF212121)
    val White = Color(0xFFFFFFFF)
    val PureBlack = Color(0xFF000000)
}

val DefaultTheme = darkColorScheme(
    background = AppColor.Black,
    onBackground = AppColor.White,
)

val LightTheme = lightColorScheme(
    background = AppColor.White,
    onBackground = AppColor.Black,
)

val DarkTheme = darkColorScheme(
    background = AppColor.PureBlack,
    onBackground = AppColor.White,
)