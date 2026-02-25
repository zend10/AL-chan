package com.zen.alchan

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.sourcesanspro_regular
import al_chan.composeapp.generated.resources.sourcesanspro_semibold
import al_chan.composeapp.generated.resources.ubuntu_medium
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zen.alchan.data.enums.AppTheme
import com.zen.alchan.theme.AppDimen
import com.zen.alchan.theme.DarkTheme
import com.zen.alchan.theme.DefaultTheme
import com.zen.alchan.theme.LightTheme
import org.jetbrains.compose.resources.Font

@Composable
fun ALChanTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    content: @Composable () -> Unit
) {
    val ubuntuMedium = FontFamily(Font(Res.font.ubuntu_medium))
    val sourceSansProSemiBold = FontFamily(Font(Res.font.sourcesanspro_semibold))
    val sourceSansProRegular = FontFamily(Font(Res.font.sourcesanspro_regular))
    val typography = Typography(
        titleLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = ubuntuMedium),
        titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = sourceSansProSemiBold),
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = sourceSansProRegular),
        bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = sourceSansProRegular),
    )

    val dimen = AppDimen(
        paddingVeryBig = 48.dp,
        paddingBig = 24.dp,
        paddingNormal = 16.dp,
        paddingSmall = 12.dp,
        paddingVerySmall = 8.dp,
        iconNormal = 24.dp,
        iconSmall = 16.dp,
    )

    val colorScheme = when (appTheme) {
        AppTheme.DEFAULT -> DefaultTheme
        AppTheme.LIGHT -> LightTheme
        AppTheme.DARK -> DarkTheme
        AppTheme.ANILIST_LIGHT -> LightTheme
        AppTheme.ANILIST_DARK -> DarkTheme
    }

    CompositionLocalProvider(
        LocalDimen provides dimen,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
        )
    }
}

object AppTheme {
    val dimen: AppDimen
        @Composable
        get() = LocalDimen.current
}

val LocalDimen = staticCompositionLocalOf {
    AppDimen(
        paddingVeryBig = Dp.Unspecified,
        paddingBig = Dp.Unspecified,
        paddingNormal = Dp.Unspecified,
        paddingSmall = Dp.Unspecified,
        paddingVerySmall = Dp.Unspecified,
        iconNormal = Dp.Unspecified,
        iconSmall = Dp.Unspecified,
    )
}