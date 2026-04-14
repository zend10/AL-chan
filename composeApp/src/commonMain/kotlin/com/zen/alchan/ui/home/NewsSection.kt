package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.chapter
import al_chan.composeapp.generated.resources.episode
import al_chan.composeapp.generated.resources.latest_update
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.DefaultTheme
import com.zen.alchan.data.enums.MediaType
import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.News
import com.zen.alchan.data.model.api.Media
import com.zen.alchan.data.model.api.MediaTitle
import com.zen.alchan.helper.applyGradientOverlay
import com.zen.alchan.helper.applyWidthFromScreenWidth
import com.zen.alchan.helper.isWideScreen
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.DefaultCard
import com.zen.alchan.ui.component.DefaultImage
import com.zen.alchan.ui.component.DisplayText
import com.zen.alchan.ui.component.LoadingIndicator
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewsSection(
    isLoading: Boolean,
    news: List<News>,
    appConfig: AppConfig,
    onClick: (Media) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState)
    val aspectRatio = if (isWideScreen()) 2.5f else 4f / 3f

    Column(modifier = Modifier.fillMaxWidth()) {
        DisplayText(
            text = stringResource(Res.string.latest_update),
            textStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(
                top = DefaultTheme.dimen.paddingVeryBig,
                bottom = DefaultTheme.dimen.paddingNormal,
                start = DefaultTheme.dimen.paddingNormal,
                end = DefaultTheme.dimen.paddingNormal
            ),
        )

        if (isLoading) {
            LoadingIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = DefaultTheme.dimen.paddingNormal)
            )
            return
        }

        LazyRow(
            state = lazyListState,
            flingBehavior = snapFlingBehavior,
            horizontalArrangement = Arrangement.spacedBy(DefaultTheme.dimen.paddingNormal),
            contentPadding = PaddingValues(horizontal = DefaultTheme.dimen.paddingNormal)
        ) {
            items(
                news,
                key = { it.id }
            ) {
                DefaultCard(
                    modifier = Modifier
                        .applyWidthFromScreenWidth(0.8f)
                        .aspectRatio(aspectRatio)
                        .clickable { onClick(it.media) }
                ) {
                    Box {
                        DefaultImage(
                            imageUrl = it.getImage(appConfig),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(aspectRatio)
                                .applyGradientOverlay()
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(DefaultTheme.dimen.paddingNormal)
                        ) {
                            DisplayText(
                                it.getTitle(appConfig),
                                MaterialTheme.typography.titleMedium,
                                maxLines = 2
                            )
                            DisplayText(
                                it.getSubtitle(
                                    if (it.media.type == MediaType.MANGA)
                                        stringResource(Res.string.chapter)
                                    else
                                        stringResource(Res.string.episode)
                                ),
                                MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = DefaultTheme.dimen.paddingVerySmall)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewScreen_NewsSection() {
    val media = Media(title = MediaTitle(userPreferred = "Hello, World!"))
    val newsList = listOf(News("1", media), News("2", media), News("3", media))
    PreviewScreen {
        NewsSection(isLoading = false, newsList, AppConfig(), {})
    }
}