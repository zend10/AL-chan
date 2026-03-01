package com.zen.alchan.ui.main

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.agumon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Main

fun NavGraphBuilder.mainDestination() {
    composable<Main> { MainScreen() }
}

fun NavController.navigateToMain() {
    navigate(Main)
}

@Composable
fun MainScreen() {
    val viewModel = koinViewModel<MainViewModel>()
    val state by viewModel.state.collectAsState()

    val navController = rememberNavController()
    val tabs = listOf(
        Home,
        AnimeList,
        MangaList
    )

    Scaffold(
        bottomBar = { MainBottomBar(navController) }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            navController = navController,
            startDestination = tabs[state.defaultTabIndex],
        ) {
            homeDestination()
            animeListDestination()
            mangaListDestination()
        }
    }
}

@Composable
private fun MainBottomBar(navController: NavHostController) {
    BottomAppBar(
        modifier = Modifier
    ) {
        IconButton(
            onClick = { navController.navigateToHome() }
        ) {
            Image(
                painterResource(Res.drawable.agumon),
                contentDescription = "Home",
            )
        }
        IconButton(
            onClick = { navController.navigateToAnimeList() }
        ) {
            Image(
                painterResource(Res.drawable.agumon),
                contentDescription = "Home",
            )
        }
        IconButton(
            onClick = { navController.navigateToMangaList() }
        ) {
            Image(
                painterResource(Res.drawable.agumon),
                contentDescription = "Home",
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
        MainScreen()
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true
)
fun PreviewTable_MainScreen() {
    PreviewScreen {
        MainScreen()
    }
}