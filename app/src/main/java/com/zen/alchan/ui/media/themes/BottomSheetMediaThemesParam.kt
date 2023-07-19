package com.zen.alchan.ui.media.themes

import com.zen.alchan.data.response.AnimeTheme
import com.zen.alchan.data.response.AnimeThemeEntry
import com.zen.alchan.data.response.anilist.Media

data class BottomSheetMediaThemesParam(
    val media: Media,
    val animeTheme: AnimeTheme,
    val animeThemeEntry: AnimeThemeEntry?
)