package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.chapter
import al_chan.composeapp.generated.resources.episode
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.DefaultTheme
import com.zen.alchan.data.enums.MediaType
import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.api.AiringSchedule
import com.zen.alchan.data.model.api.Media
import com.zen.alchan.data.model.api.MediaTitle
import com.zen.alchan.helper.applyGradientOverlay
import com.zen.alchan.helper.applyWidthFromScreenWidth
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.DefaultCard
import com.zen.alchan.ui.component.DefaultImage
import com.zen.alchan.ui.component.DisplayText
import com.zen.alchan.ui.component.LoadingIndicator
import org.jetbrains.compose.resources.stringResource

@Composable
fun SwimlaneSection(
    isLoading: Boolean,
    swimlaneTitle: String,
    swimlaneMedia: List<Media>,
    appConfig: AppConfig,
    mediaType: MediaType,
    onMediaClick: (Media) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(
                    top = DefaultTheme.dimen.paddingVeryBig,
                    bottom = DefaultTheme.dimen.paddingNormal,
                    start = DefaultTheme.dimen.paddingNormal,
                    end = DefaultTheme.dimen.paddingNormal
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisplayText(
                text = swimlaneTitle,
                textStyle = MaterialTheme.typography.titleLarge,
            )
        }

        if (isLoading) {
            LoadingIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = DefaultTheme.dimen.paddingNormal)
            )
        } else {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(horizontal = DefaultTheme.dimen.paddingNormal),
                horizontalArrangement = Arrangement.spacedBy(DefaultTheme.dimen.paddingNormal)
            ) {
                repeat(swimlaneMedia.size) { index ->
                    val swimlaneItem = swimlaneMedia[index]
                    SwimlaneItem(
                        swimlaneItem,
                        appConfig,
                        mediaType,
                        onMediaClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SwimlaneItem(
    media: Media,
    appConfig: AppConfig,
    mediaType: MediaType,
    onClick: (Media) -> Unit
) {
    val subtitle = when (mediaType) {
        MediaType.ANIME -> {
            val studio = media.studios
                .filter { it.isMain }
                .joinToString(", ") { it.name }
            val episode =
                media.getCurrentEpisode()?.let { "${stringResource(Res.string.episode)} $it" } ?: ""
            listOf(studio, episode).filter { it.isNotBlank() }.joinToString(" • ")
        }

        MediaType.MANGA -> {
            val mainRoles = listOf("story", "art")
            val author = media.staff
                .filter { staff -> mainRoles.any { staff.role.lowercase().contains(it) } }
                .joinToString(", ") { it.name.full }
            val chapter = media.chapters?.let { "${stringResource(Res.string.chapter)} $it" } ?: ""
            listOf(author, chapter).filter { it.isNotBlank() }.joinToString(" • ")
        }
    }
    DefaultCard(
        modifier = Modifier
            .applyWidthFromScreenWidth(0.8f)
            .aspectRatio(4f / 3f)
            .clickable { onClick(media) }

    ) {
        Box {
            DefaultImage(
                imageUrl = media.bannerImage.ifBlank { media.getCoverImage(appConfig) },
                contentDescription = media.getTitle(appConfig),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .applyGradientOverlay()
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(DefaultTheme.dimen.paddingNormal)
            ) {
                DisplayText(
                    media.getTitle(appConfig),
                    MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
                DisplayText(
                    subtitle,
                    MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = DefaultTheme.dimen.paddingVerySmall)
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewScreen_SwimlaneSection() {
    val media = Media(title = MediaTitle(userPreferred = "Hello, World!"))
    PreviewScreen {
        Column {
            SwimlaneSection(
                isLoading = false,
                swimlaneTitle = "Trending Anime",
                swimlaneMedia = listOf(
                    media.copy(episodes = 10),
                    media.copy(nextAiringEpisode = AiringSchedule(1))
                ),
                appConfig = AppConfig(),
                mediaType = MediaType.ANIME,
                onMediaClick = {},
            )
        }
    }
}