package com.zen.alchan.helper

import android.graphics.Color
import com.zen.alchan.BuildConfig
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppColorTheme
import type.*

object Constant {
    const val ANILIST_API_URL = "https://graphql.anilist.co"
    const val ANILIST_URL = "https://anilist.co/"

    private const val ANILIST_CLIENT_ID = 1988
    const val ANILIST_LOGIN_URL = "${ANILIST_URL}api/v2/oauth/authorize?client_id=${ANILIST_CLIENT_ID}&response_type=token"

    const val ANILIST_REGISTER_URL = "${ANILIST_URL}signup"
    const val ANILIST_REVIEW_URL = "${ANILIST_URL}review/"
    const val ANILIST_NOTIFICATIONS_URL = "${ANILIST_URL}notifications"
    const val ANILIST_SETTINGS_URL = "${ANILIST_URL}settings/"
    const val ANILIST_ACCOUNT_URL = "${ANILIST_SETTINGS_URL}account"
    const val ANILIST_IMPORT_LISTS_URL = "${ANILIST_SETTINGS_URL}import"

    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
    const val EMAIL_ADDRESS = "alchanapp@gmail.com"
    const val TWITTER_ACCOUNT = "@alchan_app"
    const val TWITTER_URL = "https://twitter.com/alchan_app"
    const val GITHUB_URL = "https://github.com/zend10/AL-chan"
    const val PRIVACY_POLICY_URL = "https://zend10.github.io/AL-chan/privacy.html"
    const val ALCHAN_THREAD_URL = "${ANILIST_URL}forum/thread/12889"

    const val RAW_GITHUB_URL = "https://raw.githubusercontent.com/zend10/AL-chan/master/"
    const val VIDEO_THUMBNAIL_URL = "${RAW_GITHUB_URL}docs/images/video_thumbnail.png"
    const val YOUTUBE_THUMBNAIL_URL = "${RAW_GITHUB_URL}docs/images/youtube_thumbnail.png"

    const val SHARED_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + ".LocalStorage"
    const val ANIME_LIST_BACKGROUND_FILENAME = "anime_list_background.jpg"
    const val MANGA_LIST_BACKGROUND_FILENAME = "manga_list_background.jpg"

    const val FILTER_EARLIEST_YEAR = 1950

    const val DEFAULT_DATE_FORMAT = "dd MMM yyyy"
    const val DATE_TIME_FORMAT = "E, dd MMM yyyy, hh:mm a"
    const val ISO_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss"

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

    val EXTERNAL_LINK = hashMapOf(
        Pair("anilist", Pair(R.drawable.ic_anilist, Color.parseColor("#324760"))),
        Pair("twitter", Pair(R.drawable.ic_twitter, Color.parseColor("#03A9F4"))),
        Pair("crunchyroll", Pair(R.drawable.ic_crunchyroll, Color.parseColor("#FF9100"))),
        Pair("funimation", Pair(null, Color.parseColor("#452C8A"))),
        Pair("hidive", Pair(null, Color.parseColor("#03A8EB"))),
        Pair("vrv", Pair(null, Color.parseColor("#FEDD01"))),
        Pair("netflix", Pair(null, Color.parseColor("#F44335"))),
        Pair("amazon", Pair(null, Color.parseColor("#04A3DD"))),
        Pair("hulu", Pair(null, Color.parseColor("#8AC34A"))),
        Pair("animelab", Pair(null, Color.parseColor("#3B0087"))),
        Pair("viz", Pair(null, Color.parseColor("#FF0000"))),
        Pair("midnight pulp", Pair(null, Color.parseColor("#B7F00F"))),
        Pair("tubi tv", Pair(null, Color.parseColor("#F84C18"))),
        Pair("contv", Pair(null, Color.parseColor("#E35623"))),
        Pair("manga plus", Pair(null, Color.parseColor("#DC0812"))),
        Pair("manga.club", Pair(null, Color.parseColor("#F47D30"))),
        Pair("fakku", Pair(null, Color.parseColor("#911918"))),
        Pair("webtoons", Pair(null, Color.parseColor("#04CF62"))),
        Pair("lezhin", Pair(null, Color.parseColor("#E71D31"))),
        Pair("toomics", Pair(null, Color.parseColor("#EB2C2C"))),
        Pair("web comics", Pair(null, Color.parseColor("#F7745E"))),
        Pair("comicwalker (jp)", Pair(null, Color.parseColor("#F80003"))),
        Pair("pixiv comic (jp)", Pair(null, Color.parseColor("#088ED5"))),
        Pair("comico (jp)", Pair(null, Color.parseColor("#EE0208"))),
        Pair("mangabox (jp)", Pair(null, Color.parseColor("#3999B8"))),
        Pair("pixiv novel (jp)", Pair(null, Color.parseColor("#088ED5"))),
        Pair("piccoma (jp)", Pair(null, Color.parseColor("#F3C016"))),
        Pair("pocket magazine (jp)", Pair(null, Color.parseColor("#0C2F89"))),
        Pair("nico nico seiga (jp)", Pair(null, Color.parseColor("#323232"))),
        Pair("shonen jump plus (jp)", Pair(null, Color.parseColor("#E60109"))),
        Pair("naver (ko)", Pair(null, Color.parseColor("#00CE63"))),
        Pair("daum webtoon (ko)", Pair(null, Color.parseColor("#F82E40"))),
        Pair("bomtoon (ko)", Pair(null, Color.parseColor("#F82BA8"))),
        Pair("kakaopage (ko)", Pair(null, Color.parseColor("#F8CD01"))),
        Pair("kuaikan manhua (cn)", Pair(null, Color.parseColor("#F8D00B"))),
        Pair("qq (cn)", Pair(null, Color.parseColor("#FB9144"))),
        Pair("dajiaochong manhua (cn)", Pair(null, Color.parseColor("#E9CE0E"))),
        Pair("weibo manhua (cn)", Pair(null, Color.parseColor("#E0172B"))),
        Pair("manman manhua (cn)", Pair(null, Color.parseColor("#FF5746")))
    )

    // current, planning, completed, dropped, paused
    val STATUS_COLOR_LIST = arrayListOf(
        Color.parseColor("#f89963"),
        Color.parseColor("#05a9ff"),
        Color.parseColor("#69d83a"),
        Color.parseColor("#9256f3"),
        Color.parseColor("#f87aa5")
    )

    val STATUS_COLOR_MAP = hashMapOf(
        Pair(MediaListStatus.CURRENT, STATUS_COLOR_LIST[0]),
        Pair(MediaListStatus.PLANNING, STATUS_COLOR_LIST[1]),
        Pair(MediaListStatus.COMPLETED, STATUS_COLOR_LIST[2]),
        Pair(MediaListStatus.DROPPED, STATUS_COLOR_LIST[3]),
        Pair(MediaListStatus.PAUSED, STATUS_COLOR_LIST[4])
    )

    // from 1 - 10
    val SCORE_COLOR_LIST = arrayListOf(
        Color.parseColor("#d2492d"),
        Color.parseColor("#d2642c"),
        Color.parseColor("#d2802e"),
        Color.parseColor("#d29d2f"),
        Color.parseColor("#d2b72e"),
        Color.parseColor("#d3d22e"),
        Color.parseColor("#b8d22c"),
        Color.parseColor("#9cd42e"),
        Color.parseColor("#81d12d"),
        Color.parseColor("#63d42e")
    )

    val SCORE_COLOR_MAP = hashMapOf(
        Pair(10, SCORE_COLOR_LIST[0]),
        Pair(20, SCORE_COLOR_LIST[1]),
        Pair(30, SCORE_COLOR_LIST[2]),
        Pair(40, SCORE_COLOR_LIST[3]),
        Pair(50, SCORE_COLOR_LIST[4]),
        Pair(60, SCORE_COLOR_LIST[5]),
        Pair(70, SCORE_COLOR_LIST[6]),
        Pair(80, SCORE_COLOR_LIST[7]),
        Pair(90, SCORE_COLOR_LIST[8]),
        Pair(100, SCORE_COLOR_LIST[9])
    )

    val PIE_CHART_COLOR_LIST = arrayListOf(
        Color.parseColor("#55e2cf"),
        Color.parseColor("#57aee2"),
        Color.parseColor("#5668e2"),
        Color.parseColor("#8a56e2"),
        Color.parseColor("#ce56e2"),
        Color.parseColor("#e256ae"),
        Color.parseColor("#e25768"),
        Color.parseColor("#e28956"),
        Color.parseColor("#e3cf56"),
        Color.parseColor("#aee256"),
        Color.parseColor("#68e257"),
        Color.parseColor("#56e28a")
    )

    // current, planning, completed, dropped, paused
    val PRIORITY_COLOR_MAP = hashMapOf(
        Pair(1, Color.parseColor("#FF0000")),
        Pair(2, Color.parseColor("#FFFF00")),
        Pair(3, Color.parseColor("#00FF00")),
        Pair(4, Color.parseColor("#00FFFF")),
        Pair(5, Color.parseColor("#0000FF"))
    )
}