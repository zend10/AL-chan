package com.zen.alchan.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object AppColor {
    val Black = Color(0xFF212121)
    val White = Color(0xFFFFFFFF)
    val PureBlack = Color(0xFF000000)

    val Yellow = Color(0xFFFCF1C2)
    val Cyan = Color(0xFFDCFDF9)
    val Magenta = Color(0xFFC987C4)

    val LightBlue = Color(0xFF007AFF)
    val LightCream = Color(0xFFFF2A19)
    val LightGold = Color(0xFF9DB309)

    val TransparentYellow = Color(0x80FCF1C2)
    val TransparentBlack = Color(0x80212121)

    val BlackLight = Color(0xFF2F2F2F)
}

val BrandTheme = darkColorScheme(
    primary = AppColor.Yellow,
    secondary = AppColor.Cyan,
    error = AppColor.Magenta,
    background = AppColor.Black,
    onBackground = AppColor.White,
    surface = AppColor.BlackLight,
    surfaceDim = AppColor.TransparentBlack,
    primaryFixedDim = AppColor.TransparentYellow,
)

val LightTheme = lightColorScheme(
    primary = AppColor.LightBlue,
    secondary = AppColor.LightCream,
    error = AppColor.LightGold,
    background = AppColor.White,
    onBackground = AppColor.Black,
)

val DarkTheme = darkColorScheme(
    primary = AppColor.Yellow,
    secondary = AppColor.Cyan,
    error = AppColor.Magenta,
    background = AppColor.PureBlack,
    onBackground = AppColor.White,
)

val AniListLightTheme = lightColorScheme(

)

val AniListDarkTheme = darkColorScheme(

)