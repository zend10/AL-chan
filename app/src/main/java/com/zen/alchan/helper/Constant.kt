package com.zen.alchan.helper

import com.zen.alchan.BuildConfig
import com.zen.alchan.helper.enums.AppColorTheme

object Constant {
    const val ANILIST_URL = "https://anilist.co/"
    const val ANILIST_REGISTER_URL = "${ANILIST_URL}signup"

    const val ANILIST_API_URL = "https://graphql.anilist.co"

    private const val ANILIST_CLIENT_ID = 1988
    const val ANILIST_LOGIN_URL = "${ANILIST_URL}api/v2/oauth/authorize?client_id=${ANILIST_CLIENT_ID}&response_type=token"

    const val SHARED_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + ".LocalStorage"

    val DEFAULT_THEME = AppColorTheme.YELLOW
    val DEFAULT_SPLIT_ANIME_LIST_ORDER = listOf(
        "Watching",
        "Rewatching",
        "Completed TV",
        "Completed Movie",
        "Completed OVA",
        "Completed ONA",
        "Completed TV Short",
        "Completed Special",
        "Completed Music",
        "Paused",
        "Dropped",
        "Planning"
    )

    val DEFAULT_ANIME_LIST_ORDER = listOf(
        "Watching",
        "Rewatching",
        "Completed",
        "Paused",
        "Dropped",
        "Planning"
    )

    val DEFAULT_SPLIT_MANGA_LIST_ORDER = listOf(
        "Reading",
        "Rereading",
        "Completed Manga",
        "Completed Novel",
        "Completed One Shot",
        "Paused",
        "Dropped",
        "Planning"
    )

    val DEFAULT_MANGA_LIST_ORDER = listOf(
        "Reading",
        "Rereading",
        "Completed",
        "Paused",
        "Dropped",
        "Planning"
    )

    const val DATE_FORMAT = "dd MMM yyyy"
    const val DATE_TIME_FORMAT = "E, dd MMM yyyy, hh:mm a"
}