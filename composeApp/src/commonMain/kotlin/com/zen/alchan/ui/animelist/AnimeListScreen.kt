package com.zen.alchan.ui.animelist

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object AnimeList

fun NavGraphBuilder.animeListDestination() {
    composable<AnimeList> { AnimeListScreen() }
}

fun NavController.navigateToAnimeList() {
    navigate(AnimeList)
}

@Composable
fun AnimeListScreen() {
    Text("Anime List")
}