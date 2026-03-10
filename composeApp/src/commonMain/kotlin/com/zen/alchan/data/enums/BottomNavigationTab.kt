package com.zen.alchan.data.enums

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.anime
import al_chan.composeapp.generated.resources.home
import al_chan.composeapp.generated.resources.ic_anime
import al_chan.composeapp.generated.resources.ic_home
import al_chan.composeapp.generated.resources.ic_manga
import al_chan.composeapp.generated.resources.manga
import com.zen.alchan.ui.main.MainUiEffect
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class BottomNavigationTab(
    val title: StringResource,
    val icon: DrawableResource,
    val onTabSelectedEffect: MainUiEffect,
    val onTabReselectedEffect: MainUiEffect
) {
    HOME(
        Res.string.home,
        Res.drawable.ic_home,
        MainUiEffect.NavigateToHome,
        MainUiEffect.ScrollHomeToTop
    ),
    ANIME_LIST(
        Res.string.anime,
        Res.drawable.ic_anime,
        MainUiEffect.NavigateToAnimeList,
        MainUiEffect.ScrollAnimeListToTop
    ),
    MANGA_LIST(
        Res.string.manga,
        Res.drawable.ic_manga,
        MainUiEffect.NavigateToMangaList,
        MainUiEffect.ScrollMangaListToTop
    )
}