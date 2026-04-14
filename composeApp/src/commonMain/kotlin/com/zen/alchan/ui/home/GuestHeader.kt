@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.guest_greetings_body
import al_chan.composeapp.generated.resources.guest_greetings_title
import al_chan.composeapp.generated.resources.guest_wallpaper
import al_chan.composeapp.generated.resources.log_in
import al_chan.composeapp.generated.resources.login_body
import al_chan.composeapp.generated.resources.login_title
import al_chan.composeapp.generated.resources.register
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.zen.alchan.DefaultTheme
import com.zen.alchan.helper.applyGradientOverlay
import com.zen.alchan.helper.getScreenWidth
import com.zen.alchan.helper.isWideScreen
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.DefaultImage
import com.zen.alchan.ui.component.DisplayText
import com.zen.alchan.ui.component.MarkdownText
import com.zen.alchan.ui.component.PrimaryButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun GuestHeader(
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    onClickRegister: () -> Unit,
    onClickLogin: () -> Unit
) {
    val guestLoginRichTextState = rememberRichTextState()
    val guestText = stringResource(Res.string.login_body)
    val aspectRatio = if (isWideScreen()) 2.5f else 1f
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

    LaunchedEffect(Unit) {
        guestLoginRichTextState.setMarkdown(guestText)
    }

    Box {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .height(
                    bannerHeight + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
        ) {
            DefaultImage(
                drawableResource = Res.drawable.guest_wallpaper,
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
                    DisplayText(
                        text = stringResource(Res.string.guest_greetings_title),
                        textStyle = MaterialTheme.typography.titleMedium,
                    )
                    DisplayText(
                        text = stringResource(Res.string.guest_greetings_body),
                        textStyle = MaterialTheme.typography.bodySmall,
                    )
                    DisplayText(
                        text = stringResource(Res.string.login_title),
                        textStyle = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = DefaultTheme.dimen.paddingNormal)
                    )
                    MarkdownText(
                        richTextState = guestLoginRichTextState,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = DefaultTheme.dimen.paddingNormal),
                    horizontalArrangement = Arrangement.spacedBy(
                        DefaultTheme.dimen.paddingNormal,
                        Alignment.End
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClickableText(
                        text = stringResource(Res.string.register),
                        onClick = { onClickRegister() },
                        textStyle = MaterialTheme.typography.bodyLarge,
                    )
                    PrimaryButton(
                        text = stringResource(Res.string.log_in),
                        onClick = { onClickLogin() },
                    )
                }
            }
        }
        LargeTopAppBar(
            modifier = Modifier.padding(0.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            title = {},
            scrollBehavior = topAppBarScrollBehavior
        )
    }
}

@Composable
@Preview
fun PreviewScreen_GuestHeader() {
    PreviewScreen { GuestHeader(TopAppBarDefaults.exitUntilCollapsedScrollBehavior(), {}, {}) }
}