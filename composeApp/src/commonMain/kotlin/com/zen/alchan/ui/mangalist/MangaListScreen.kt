package com.zen.alchan.ui.mangalist

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object MangaList

fun NavGraphBuilder.mangaListDestination() {
    composable<MangaList> { MangaListScreen() }
}

fun NavController.navigateToMangaList() {
    navigate(MangaList)
}

@Composable
fun MangaListScreen() {
    Text("Manga List")
}