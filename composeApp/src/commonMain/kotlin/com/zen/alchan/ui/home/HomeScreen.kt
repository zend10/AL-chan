package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.calendar
import al_chan.composeapp.generated.resources.explore
import al_chan.composeapp.generated.resources.guest_greetings_body
import al_chan.composeapp.generated.resources.guest_greetings_title
import al_chan.composeapp.generated.resources.ic_calendar
import al_chan.composeapp.generated.resources.ic_explore
import al_chan.composeapp.generated.resources.ic_seasonal
import al_chan.composeapp.generated.resources.ic_social
import al_chan.composeapp.generated.resources.log_in
import al_chan.composeapp.generated.resources.login_body
import al_chan.composeapp.generated.resources.login_title
import al_chan.composeapp.generated.resources.register
import al_chan.composeapp.generated.resources.seasonal
import al_chan.composeapp.generated.resources.social
import al_chan.composeapp.generated.resources.trending_title
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil3.compose.AsyncImage
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.zen.alchan.DefaultTheme
import com.zen.alchan.data.model.api.Media
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.Card
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.DisplayText
import com.zen.alchan.ui.component.Image
import com.zen.alchan.ui.component.LoadingIndicator
import com.zen.alchan.ui.component.MarkdownText
import com.zen.alchan.ui.component.PrimaryButton
import com.zen.alchan.ui.main.MainUiEffect
import com.zen.alchan.ui.main.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Home

fun NavGraphBuilder.homeDestination(mainViewModel: MainViewModel) {
    composable<Home> { HomeScreen(mainViewModel) }
}

fun NavController.navigateToHome() {
    navigate(Home)
}

@Composable
fun HomeScreen(mainViewModel: MainViewModel?) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        mainViewModel?.bottomNavigationTabEffect?.collectLatest { newEffect ->
            when (newEffect) {
                MainUiEffect.ScrollHomeToTop -> scrollState.animateScrollTo(0)
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { newEffect ->
            when (newEffect) {
                HomeUiEffect.NavigateToCalendar -> {}
                HomeUiEffect.NavigateToExplore -> {}
                HomeUiEffect.NavigateToSeasonal -> {}
                HomeUiEffect.NavigateToSocial -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.user.isGuest()) {
            GuestHeader()
        } else {
            Header()
        }
        QuickMenu(viewModel)
        TrendingSection(
            state.isLoading,
            state.homeData.trendingAnime.media,
            state.homeData.trendingManga.media
        )
    }
}

@Composable
private fun GuestHeader() {
    val guestLoginRichTextState = rememberRichTextState()
    val guestText = stringResource(Res.string.login_body)

    LaunchedEffect(Unit) {
        guestLoginRichTextState.setMarkdown(guestText)
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(DefaultTheme.dimen.paddingNormal)
    ) {
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = DefaultTheme.dimen.paddingNormal),
            horizontalArrangement = Arrangement.spacedBy(
                DefaultTheme.dimen.paddingNormal,
                Alignment.End
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClickableText(
                text = stringResource(Res.string.register),
                onClick = { },
                textStyle = MaterialTheme.typography.bodyLarge
            )
            PrimaryButton(
                text = stringResource(Res.string.log_in),
                onClick = { },
            )
        }
    }
}

@Composable
private fun Header() {

}

@Composable
private fun QuickMenu(viewModel: HomeViewModel) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = DefaultTheme.dimen.paddingNormal)
            .padding(top = DefaultTheme.dimen.paddingVeryBig),
        horizontalArrangement = Arrangement.spacedBy(DefaultTheme.dimen.paddingVerySmall)
    ) {
        QuicKMenuItem(
            Res.string.seasonal,
            Res.drawable.ic_seasonal,
            onClick = { viewModel.onSeasonalPressed() }
        )
        QuicKMenuItem(
            Res.string.explore,
            Res.drawable.ic_explore,
            onClick = { viewModel.onExplorePressed() }
        )
        QuicKMenuItem(
            Res.string.calendar,
            Res.drawable.ic_calendar,
            onClick = { viewModel.onCalendarPressed() }
        )
        QuicKMenuItem(
            Res.string.social,
            Res.drawable.ic_social,
            onClick = { viewModel.onSocialPressed() }
        )
    }
}

@Composable
private fun QuicKMenuItem(
    label: StringResource,
    icon: DrawableResource,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Card(
            shape = CircleShape
        ) {
            Image(
                drawableResource = icon,
                contentDescription = stringResource(label),
                modifier = Modifier.padding(DefaultTheme.dimen.paddingNormal),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
        ClickableText(
            text = stringResource(label),
            textStyle = MaterialTheme.typography.bodySmall,
            onClick = { },
            modifier = Modifier.padding(top = DefaultTheme.dimen.paddingVerySmall)
        )
    }
}

@Composable
private fun TrendingSection(
    isLoading: Boolean,
    trendingAnime: List<Media>,
    trendingManga: List<Media>
) {
    Column {
        DisplayText(
            text = stringResource(Res.string.trending_title),
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = DefaultTheme.dimen.paddingNormal)
                .padding(bottom = DefaultTheme.dimen.paddingNormal)
                .padding(top = DefaultTheme.dimen.paddingVeryBig)
        )
        if (isLoading) {
            LoadingIndicator()
        } else {
            if (trendingAnime.isNotEmpty()) {
                Card(
                    modifier = Modifier.padding(horizontal = DefaultTheme.dimen.paddingNormal)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(18f / 16f),
                    ) {
                        val shapeLeft = GenericShape { size, layoutDirection ->
                            val width = size.width
                            val height = size.height
                            moveTo(0f, height)
                            lineTo(0f, 0f)
                            lineTo(width * 0.6f, 0f)
                            lineTo(width * 0.4f, height)
                            close()
                        }

                        val shapeRight = GenericShape { size, layoutDirection ->
                            val width = size.width
                            val height = size.height
                            moveTo(width, 0f)
                            lineTo(width, height)
                            lineTo(width * 0.4f, height)
                            lineTo(width * 0.6f, 0f)
                            close()
                        }

                        val modifierLeft = Modifier
                            .fillMaxWidth()
                            .aspectRatio(9f / 16f)
                            .align(Alignment.TopStart)
                            .graphicsLayer {
                                clip = true
                                shape = shapeLeft
                            }

                        val modifierRight = Modifier
                            .fillMaxWidth()
                            .aspectRatio(9f / 16f)
                            .align(Alignment.TopEnd)
                            .graphicsLayer {
                                clip = true
                                shape = shapeRight
                            }

                        Image(
                            imageUrl = trendingAnime[0].coverImage.extraLarge,
                            contentDescription = trendingAnime[0].title.userPreferred,
                            modifier = modifierLeft,
                            contentScale = ContentScale.Fit
                        )

                        Image(
                            imageUrl = trendingAnime[1].coverImage.extraLarge,
                            contentDescription = trendingAnime[1].title.userPreferred,
                            modifier = modifierRight,
                            contentScale = ContentScale.Fit
                        )
                    }

                }
            }


//            repeat(trending.size) { index ->
//                val trendingItem = trending[index]
//                TrendingItem(
//                    trendingItem.title.userPreferred,
//                    "Episode ${trendingItem.episodes ?: 0}",
//                    trendingItem.bannerImage
//                )
//            }
        }
    }
}

@Composable
fun TrendingItem(
    title: String,
    caption: String,
    imageUrl: String
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            imageUrl,
            contentDescription = "",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.5f)
                .drawWithCache {
                    val gradient = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = size.height / 3,
                        endY = size.height,
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(gradient, blendMode = BlendMode.Multiply)
                    }
                }
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(DefaultTheme.dimen.paddingNormal)
        ) {
            DisplayText(title, textStyle = MaterialTheme.typography.titleLarge)
            DisplayText(caption, textStyle = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true
)
fun PreviewPhone_HomeScreen() {
    PreviewScreen {
        HomeScreen(null)
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTablet_HomeScreen() {
    PreviewScreen {
        HomeScreen(null)
    }
}