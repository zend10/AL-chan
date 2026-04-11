package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.new_anime
import al_chan.composeapp.generated.resources.new_manga
import al_chan.composeapp.generated.resources.trending_anime
import al_chan.composeapp.generated.resources.trending_manga
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zen.alchan.DefaultTheme
import com.zen.alchan.data.enums.MediaType
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.main.MainUiEffect
import com.zen.alchan.ui.main.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Home

fun NavGraphBuilder.homeDestination(mainViewModel: MainViewModel) {
    composable<Home> { HomeScreen(mainViewModel) }
}

fun NavController.navigateToHome() {
    navigate(Home) {
        popUpTo(graph.id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
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
                is HomeUiEffect.NavigateToMediaDetail -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = DefaultTheme.dimen.paddingVeryBig)
    ) {
        if (state.user.isGuest()) {
            GuestHeader()
        } else {
            Header()
        }
        QuickMenu(
            onSeasonalPressed = { viewModel.onSeasonalPressed() },
            onExplorePressed = { viewModel.onExplorePressed() },
            onCalendarPressed = { viewModel.onCalendarPressed() },
            onSocialPressed = { viewModel.onSocialPressed() },
        )
        SwimlaneSection(
            state.isLoading,
            stringResource(Res.string.trending_anime),
            state.homeData.trendingAnime.media,
            state.appConfig,
            MediaType.ANIME,
            onMediaClick = { viewModel.onMediaPressed(it) },
        )
        SwimlaneSection(
            state.isLoading,
            stringResource(Res.string.trending_manga),
            state.homeData.trendingManga.media,
            state.appConfig,
            MediaType.MANGA,
            onMediaClick = { viewModel.onMediaPressed(it) },
        )
        SwimlaneSection(
            state.isLoading,
            stringResource(Res.string.new_anime),
            state.homeData.newAnime.media,
            state.appConfig,
            MediaType.ANIME,
            onMediaClick = { viewModel.onMediaPressed(it) },
        )
        SwimlaneSection(
            state.isLoading,
            stringResource(Res.string.new_manga),
            state.homeData.newManga.media,
            state.appConfig,
            MediaType.MANGA,
            onMediaClick = { viewModel.onMediaPressed(it) },
        )
    }
}


@Composable
private fun Header() {

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