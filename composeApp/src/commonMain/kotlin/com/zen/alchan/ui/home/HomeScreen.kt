package com.zen.alchan.ui.home

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.welcome
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zen.alchan.DefaultTheme
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.LoadingIndicator
import com.zen.alchan.ui.main.MainUiEffect
import com.zen.alchan.ui.main.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Home

fun NavGraphBuilder.homeDestination(
    mainViewModel: MainViewModel,
    onNavigateToSeasonal: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSocial: () -> Unit,
    onNavigateToWeb: (String) -> Unit
) {
    composable<Home> {
        HomeScreen(
            mainViewModel,
            onNavigateToSeasonal,
            onNavigateToExplore,
            onNavigateToCalendar,
            onNavigateToSocial,
            onNavigateToWeb
        )
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel?,
    onNavigateToSeasonal: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSocial: () -> Unit,
    onNavigateToWeb: (String) -> Unit
) {
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
                HomeUiEffect.NavigateToCalendar -> {
                    onNavigateToCalendar()
                }

                HomeUiEffect.NavigateToExplore -> {
                    onNavigateToExplore()
                }

                HomeUiEffect.NavigateToSeasonal -> {
                    onNavigateToSeasonal()
                }

                HomeUiEffect.NavigateToSocial -> {
                    onNavigateToSocial()
                }

                is HomeUiEffect.NavigateToMediaDetail -> {}
                is HomeUiEffect.NavigateToWeb -> {
                    onNavigateToWeb(newEffect.url)
                }
            }
        }
    }

    if (state.isLoggingIn) {
        LoginLoading()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = DefaultTheme.dimen.paddingVeryBig)
    ) {
        if (state.user.isGuest()) {
            GuestHeader(
                onClickRegister = { viewModel.onRegisterPressed() },
                onClickLogin = { viewModel.onLoginPressed() }
            )
        } else {
            Header()
        }
        QuickMenu(
            onSeasonalPressed = { viewModel.onSeasonalPressed() },
            onExplorePressed = { viewModel.onExplorePressed() },
            onCalendarPressed = { viewModel.onCalendarPressed() },
            onSocialPressed = { viewModel.onSocialPressed() },
        )
        NewsSection(
            state.isLoading,
            state.news,
            state.appConfig,
            onClick = { viewModel.onMediaPressed(it) }
        )
    }
}

@Composable
private fun LoginLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator(text = stringResource(Res.string.welcome))
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
        HomeScreen(null, {}, {}, {}, {}, {})
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTablet_HomeScreen() {
    PreviewScreen {
        HomeScreen(null, {}, {}, {}, {}, {})
    }
}