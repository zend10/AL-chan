@file:OptIn(ExperimentalMaterial3Api::class)

package com.zen.alchan.ui.component

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.guest_wallpaper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.zen.alchan.DefaultTheme
import com.zen.alchan.helper.applyGradientOverlay
import com.zen.alchan.helper.getScreenWidth
import com.zen.alchan.helper.isWideScreen
import com.zen.alchan.ui.common.PreviewScreen
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun CollapsingTopBar(
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    backgroundImageUrl: String? = null,
    backgroundImageDrawableResource: DrawableResource? = null,
    aspectRatio: Float = if (isWideScreen()) 2.5f else 1f,
    fullyExpandedContent: @Composable (ColumnScope.() -> Unit)? = null,
    alwaysDisplayedContent: @Composable (ColumnScope.() -> Unit)
) {
    val screenWidth = getScreenWidth()
    val collapsedAppBarMinHeight = 72.dp
    val bannerHeight by remember {
        derivedStateOf {
            val calculatedHeight =
                screenWidth / aspectRatio * (1f - topAppBarScrollBehavior.state.collapsedFraction)
            max(calculatedHeight, collapsedAppBarMinHeight)
        }
    }
    val isFullyExpanded by remember {
        derivedStateOf {
            topAppBarScrollBehavior.state.collapsedFraction == 0f
        }
    }

    Box {
        LargeTopAppBar(
            modifier = Modifier.padding(0.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            title = {},
            scrollBehavior = topAppBarScrollBehavior
        )
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .height(
                    bannerHeight + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
        ) {
            DefaultImage(
                imageUrl = backgroundImageUrl,
                drawableResource = backgroundImageDrawableResource,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        bannerHeight + WindowInsets.statusBars.asPaddingValues()
                            .calculateTopPadding()
                    )
                    .applyGradientOverlay(0f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DefaultTheme.dimen.paddingNormal)
                    .align(Alignment.BottomStart)
            ) {
                if (isFullyExpanded) {
                    fullyExpandedContent?.invoke(this)
                }
                alwaysDisplayedContent()
            }
        }
    }
}

@Composable
@Preview
fun PreviewScreen_CollapsingTopBar() {
    PreviewScreen {
        CollapsingTopBar(
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
            backgroundImageDrawableResource = Res.drawable.guest_wallpaper,
            fullyExpandedContent = {
                DisplayText("Fully Expanded Content", MaterialTheme.typography.titleLarge)
            },
            alwaysDisplayedContent = {
                DisplayText("Always Displayed Content", MaterialTheme.typography.titleLarge)
            }
        )
    }
}