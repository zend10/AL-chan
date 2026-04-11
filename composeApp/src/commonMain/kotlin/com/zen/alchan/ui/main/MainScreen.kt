package com.zen.alchan.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zen.alchan.data.enums.BottomNavigationTab
import com.zen.alchan.ui.animelist.AnimeList
import com.zen.alchan.ui.animelist.animeListDestination
import com.zen.alchan.ui.animelist.navigateToAnimeList
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.home.Home
import com.zen.alchan.ui.home.homeDestination
import com.zen.alchan.ui.home.navigateToHome
import com.zen.alchan.ui.mangalist.MangaList
import com.zen.alchan.ui.mangalist.mangaListDestination
import com.zen.alchan.ui.mangalist.navigateToMangaList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Main

fun NavGraphBuilder.mainDestination(
    onNavigateToSeasonal: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSocial: () -> Unit
) {
    composable<Main> {
        MainScreen(
            onNavigateToSeasonal,
            onNavigateToExplore,
            onNavigateToCalendar,
            onNavigateToSocial
        )
    }
}

fun NavController.navigateToMain() {
    navigate(Main) {
        popUpTo(graph.id)
    }
}

@Composable
fun MainScreen(
    onNavigateToSeasonal: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSocial: () -> Unit
) {
    val viewModel = koinViewModel<MainViewModel>()
    val state by viewModel.state.collectAsState()

    val navController = rememberNavController()
    val tabs = state.bottomNavigationTabs.map {
        when (it) {
            BottomNavigationTab.HOME -> Home
            BottomNavigationTab.ANIME_LIST -> AnimeList
            BottomNavigationTab.MANGA_LIST -> MangaList
        }
    }

    LaunchedEffect(Unit) {
        viewModel.bottomNavigationTabEffect.collectLatest { newEffect ->
            when (newEffect) {
                MainUiEffect.NavigateToHome -> navController.navigateToHome()
                MainUiEffect.NavigateToAnimeList -> navController.navigateToAnimeList()
                MainUiEffect.NavigateToMangaList -> navController.navigateToMangaList()
                else -> {}
            }
        }
    }

    Scaffold(
        bottomBar = {
            MainBottomNavigationBar(
                { tab -> viewModel.onTabPressed(tab) },
                state
            )
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            navController = navController,
            startDestination = tabs[state.defaultTabIndex],
        ) {
            homeDestination(
                viewModel,
                onNavigateToSeasonal,
                onNavigateToExplore,
                onNavigateToCalendar,
                onNavigateToSocial
            )
            animeListDestination()
            mangaListDestination()
        }
    }
}

@Composable
private fun MainBottomNavigationBar(
    onTabPressed: (tab: BottomNavigationTab) -> Unit,
    state: MainUiState
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        repeat(state.bottomNavigationTabs.size) { index ->
            val tab = state.bottomNavigationTabs[index]
            NavigationBarItem(
                selected = state.selectedTabIndex == index,
                onClick = { onTabPressed(tab) },
                icon = {
                    Image(painterResource(tab.icon), contentDescription = null)
                },
                label = {
                    Text(
                        text = stringResource(tab.title),
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onBackground,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryFixedDim,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true
)
fun PreviewPhone_MainScreen() {
    PreviewScreen {
        MainScreen({}, {}, {}, {})
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTable_MainScreen() {
    PreviewScreen {
        MainScreen({}, {}, {}, {})
    }
}