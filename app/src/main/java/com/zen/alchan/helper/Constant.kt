package com.zen.alchan.helper

import com.zen.alchan.BuildConfig
import com.zen.alchan.helper.enums.AppColorTheme
import type.*

object Constant {
    const val ANILIST_URL = "https://anilist.co/"
    const val ANILIST_REGISTER_URL = "${ANILIST_URL}signup"

    const val ANILIST_API_URL = "https://graphql.anilist.co"

    private const val ANILIST_CLIENT_ID = 1988
    const val ANILIST_LOGIN_URL = "${ANILIST_URL}api/v2/oauth/authorize?client_id=${ANILIST_CLIENT_ID}&response_type=token"

    const val SHARED_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + ".LocalStorage"
    const val ANIME_LIST_BACKGROUND_FILENAME = "anime_list_background.jpg"
    const val MANGA_LIST_BACKGROUND_FILENAME = "manga_list_background.jpg"

    const val FILTER_EARLIEST_YEAR = 1950

    const val DEFAULT_DATE_FORMAT = "dd MMM yyyy"
    const val DATE_TIME_FORMAT = "E, dd MMM yyyy, hh:mm a"

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

    val ANIME_FORMAT_LIST = listOf(
        MediaFormat.TV, MediaFormat.TV_SHORT, MediaFormat.MOVIE, MediaFormat.SPECIAL, MediaFormat.OVA, MediaFormat.ONA, MediaFormat.MUSIC
    )

    val MANGA_FORMAT_LIST = listOf(
        MediaFormat.MANGA, MediaFormat.NOVEL, MediaFormat.ONE_SHOT
    )

    val SEASON_LIST = listOf(
        MediaSeason.WINTER, MediaSeason.SPRING, MediaSeason.SUMMER, MediaSeason.FALL
    )

    val MEDIA_STATUS_LIST = listOf(
        MediaStatus.FINISHED, MediaStatus.RELEASING, MediaStatus.NOT_YET_RELEASED, MediaStatus.CANCELLED
    )

    val ANIME_SOURCE_LIST = listOf(
        MediaSource.ORIGINAL,
        MediaSource.MANGA,
        MediaSource.LIGHT_NOVEL,
        MediaSource.VISUAL_NOVEL,
        MediaSource.NOVEL,
        MediaSource.VIDEO_GAME,
        MediaSource.DOUJINSHI,
        MediaSource.OTHER
    )

    val MANGA_SOURCE_LIST = listOf(
        MediaSource.ANIME,
        MediaSource.LIGHT_NOVEL,
        MediaSource.VISUAL_NOVEL,
        MediaSource.NOVEL,
        MediaSource.VIDEO_GAME,
        MediaSource.DOUJINSHI,
        MediaSource.OTHER
    )

    val MEDIA_RELATION_PRIORITY = hashMapOf(
        Pair(MediaRelation.SOURCE, 0),
        Pair(MediaRelation.ADAPTATION, 1),
        Pair(MediaRelation.PARENT, 2),
        Pair(MediaRelation.PREQUEL, 4),
        Pair(MediaRelation.SEQUEL, 5),
        Pair(MediaRelation.ALTERNATIVE, 6),
        Pair(MediaRelation.SIDE_STORY, 7),
        Pair(MediaRelation.SPIN_OFF, 8),
        Pair(MediaRelation.SUMMARY, 9),
        Pair(MediaRelation.COMPILATION, 10),
        Pair(MediaRelation.CONTAINS, 11),
        Pair(MediaRelation.CHARACTER, 12),
        Pair(MediaRelation.OTHER, 13)
    )
}