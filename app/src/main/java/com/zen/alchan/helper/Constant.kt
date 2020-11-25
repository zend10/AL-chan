package com.zen.alchan.helper

import android.graphics.Color
import com.zen.alchan.BuildConfig
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.ColorPalette
import type.*

object Constant {
    const val ANILIST_API_URL = "https://graphql.anilist.co"
    const val ANILIST_URL = "https://anilist.co/"

    private const val ANILIST_CLIENT_ID = 1988
    const val ANILIST_LOGIN_URL = "${ANILIST_URL}api/v2/oauth/authorize?client_id=${ANILIST_CLIENT_ID}&response_type=token"

    const val ANILIST_REGISTER_URL = "${ANILIST_URL}signup"
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

    const val JIKAN_URL = "https://api.jikan.moe/v3/"
    const val YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/"
    const val SPOTIFY_AUTH_API_URL = "https://accounts.spotify.com/api/"
    const val SPOTIFY_API_URL = "https://api.spotify.com/v1/"

    const val RAW_GITHUB_URL = "https://raw.githubusercontent.com/zend10/AL-chan/master/"
    const val VIDEO_THUMBNAIL_URL = "${RAW_GITHUB_URL}docs/images/video_thumbnail.png"
    const val YOUTUBE_THUMBNAIL_URL = "${RAW_GITHUB_URL}docs/images/youtube_thumbnail.png"

    const val SHARED_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + ".LocalStorage"
    const val ANIME_LIST_BACKGROUND_FILENAME = "anime_list_background.jpg"
    const val MANGA_LIST_BACKGROUND_FILENAME = "manga_list_background.jpg"

    const val FILTER_EARLIEST_YEAR = 1950
    const val DEFAULT_MINIMUM_TAG_RANK = 18

    const val DEFAULT_DATE_FORMAT = "dd MMM yyyy"
    const val DATE_TIME_FORMAT = "E, dd MMM yyyy, hh:mm a"
    const val ISO_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss"

    const val EVA_ID = 103770

    val DEFAULT_THEME = AppColorTheme.DEFAULT_THEME_YELLOW

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
        MediaStatus.FINISHED, MediaStatus.RELEASING, MediaStatus.NOT_YET_RELEASED, MediaStatus.CANCELLED, MediaStatus.HIATUS
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
        Pair("youtube", Pair(R.drawable.ic_play_button, Color.parseColor("#FF0000"))),
        Pair("funimation", Pair(null, Color.parseColor("#452C8A"))),
        Pair("hidive", Pair(null, Color.parseColor("#03A8EB"))),
        Pair("vrv", Pair(null, Color.parseColor("#FEDD01"))),
        Pair("netflix", Pair(null, Color.parseColor("#F44335"))),
        Pair("amazon", Pair(null, Color.parseColor("#04A3DD"))),
        Pair("hulu", Pair(null, Color.parseColor("#8AC34A"))),
        Pair("hbo max", Pair(null, Color.parseColor("#9531EC"))),
        Pair("animelab", Pair(null, Color.parseColor("#3B0087"))),
        Pair("viz", Pair(null, Color.parseColor("#FF0000"))),
        Pair("adult swim", Pair(null, Color.parseColor("#171717"))),
        Pair("retro crush", Pair(null, Color.parseColor("#000000"))),
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

    val DEFAULT_GENRE_COLOR = "#727272"

    val GENRE_COLOR = hashMapOf(
        Pair("Action", "#24687B"),
        Pair("Adventure", "#014037"),
        Pair("Comedy", "#E6977E"),
        Pair("Drama", "#7E1416"),
        Pair("Ecchi", "#7E174A"),
        Pair("Fantasy", "#989D60"),
        Pair("Hentai", "#37286B"),
        Pair("Horror", "#5B1765"),
        Pair("Mahou Shoujo", "#BF5264"),
        Pair("Mecha", "#542437"),
        Pair("Music", "#329669"),
        Pair("Mystery", "#3D3251"),
        Pair("Psychological", "#D85C43"),
        Pair("Romance", "#C02944"),
        Pair("Sci-Fi", "#85B14B"),
        Pair("Slice of Life", "#D3B042"),
        Pair("Sports", "#6B9145"),
        Pair("Supernatural", "#338074"),
        Pair("Thriller", "#224C80")
    )

    // current, planning, completed, dropped, paused
    val STATUS_COLOR_LIST = arrayListOf(
        Color.parseColor("#3BAEEA"),
        Color.parseColor("#F79A63"),
        Color.parseColor("#7BD555"),
        Color.parseColor("#E85D75"),
        Color.parseColor("#F17575")
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

    val PRIORITY_COLOR_MAP = hashMapOf(
        Pair(1, Color.parseColor("#FF0000")),
        Pair(2, Color.parseColor("#FFFF00")),
        Pair(3, Color.parseColor("#00FF00")),
        Pair(4, Color.parseColor("#00FFFF")),
        Pair(5, Color.parseColor("#0000FF"))
    )

    val PRIORITY_LABEL_MAP = hashMapOf(
        Pair(0, "No Priority"),
        Pair(1, "Very Low"),
        Pair(2, "Low"),
        Pair(3, "Medium"),
        Pair(4, "High"),
        Pair(5, "Very High")
    )

    val ANIME_STREAMING_SITE = arrayListOf(
        Pair("crunchyroll", "Crunchyroll"),
        Pair("youtube", "Youtube"),
        Pair("funimation", "Funimation"),
        Pair("hidive", "Hidive"),
        Pair("vrv", "VRV"),
        Pair("netflix", "Netflix"),
        Pair("amazon", "Amazon"),
        Pair("hulu", "Hulu"),
        Pair("hbomax", "HBO Max"),
        Pair("animelab", "AnimeLab"),
        Pair("viz", "Viz"),
        Pair("adultswim", "Adult Swim"),
        Pair("retrocrush", "RetroCrush"),
        Pair("midnightpulp.com", "Midnight Pulp"),
        Pair("tubitv.com", "Tubi TV"),
        Pair("contv.com", "CONtv")
    )

    val MANGA_READING_SITE = arrayListOf(
        Pair("mangaplus.shueisha.co.jp", "Manga Plus"),
        Pair("viz", "Viz"),
        Pair("crunchyroll", "Crunchyroll"),
        Pair("manga.club", "Manga.Club"),
        Pair("fakku", "Fakku"),
        Pair("webtoons.com/en", "Webtoons"),
        Pair("lezhin.com/en", "Lezhin"),
        Pair("global.toomics.com", "Toomics"),
        Pair("webcomicsapp.com", "Web Comics"),
        Pair("comic-walker", "ComicWalker (JP)"),
        Pair("comic.pixiv.net", "Pixiv Comic (JP)"),
        Pair("comico.jp", "Comico (JP)"),
        Pair("mangabox", "Mangabox (JP)"),
        Pair("novel.pixiv.net", "Pixiv Novel (JP)"),
        Pair("piccoma.com", "Piccoma (JP)"),
        Pair("pocket.shonenmagazine.com", "Pocket Magazine (JP)"),
        Pair("seiga.nicovideo.jp", "Nico Nico Seiga (JP)"),
        Pair("shonenjumpplus.com", "Shonen Jump Plus (JP)"),
        Pair("lezhin.com/ko", "Lezhin (KO)"),
        Pair("naver", "Naver (KO)"),
        Pair("webtoon.daum.net", "Daum Webtoon (KO)"),
        Pair("toomics.com", "Toomics (KO)"),
        Pair("bomtoon", "Bomtoon (KO)"),
        Pair("kakao", "KakaoPage (KO)"),
        Pair("Kuaikanmanhua", "KuaiKan Manhua (CN)"),
        Pair("ac.qq.com", "QQ (CN)"),
        Pair("dajiaochongmanhua.com", "Dajiaochong Manhua (CN)"),
        Pair("manhua.weibo.com", "Weibo Manhua (CN)"),
        Pair("manmanapp.com", "Manman Manhua (CN)")
    )

    val EXTERNAL_LINK_MAP = hashMapOf(
        Pair("Crunchyroll", "crunchyroll"),
        Pair("Youtube", "youtube"),
        Pair("Funimation", "funimation"),
        Pair("Hidive", "hidive"),
        Pair("VRV", "vrv"),
        Pair("Netflix", "netflix"),
        Pair("Amazon", "amazon"),
        Pair("Hulu", "hulu"),
        Pair("HBO Max", "hbomax"),
        Pair("AnimeLab", "animelab"),
        Pair("Viz", "viz"),
        Pair("Adult Swim", "adultswim"),
        Pair("RetroCrush", "retrocrush"),
        Pair("Midnight Pulp", "midnightpulp.com"),
        Pair("Tubi TV", "tubitv.com"),
        Pair("CONtv", "contv.com"),
        Pair("Manga Plus", "mangaplus.shueisha.co.jp"),
        Pair("Viz", "viz"),
        Pair("Crunchyroll", "crunchyroll"),
        Pair("Manga.Club", "manga.club"),
        Pair("Fakku", "fakku"),
        Pair("Webtoons", "webtoons.com/en"),
        Pair("Lezhin", "lezhin.com/en"),
        Pair("Toomics", "global.toomics.com"),
        Pair("Web Comics", "webcomicsapp.com"),
        Pair("ComicWalker (JP)", "comic-walker"),
        Pair("Pixiv Comic (JP)", "comic.pixiv.net"),
        Pair("Comico (JP)", "comico.jp"),
        Pair("Mangabox (JP)", "mangabox"),
        Pair("Pixiv Novel (JP)", "novel.pixiv.net"),
        Pair("Piccoma (JP)", "piccoma.com"),
        Pair("Pocket Magazine (JP)", "pocket.shonenmagazine.com"),
        Pair("Nico Nico Seiga (JP)", "seiga.nicovideo.jp"),
        Pair("Shonen Jump Plus (JP)", "shonenjumpplus.com"),
        Pair("Lezhin (KO)", "lezhin.com/ko"),
        Pair("Naver (KO)", "naver"),
        Pair("Daum Webtoon (KO)", "webtoon.daum.net"),
        Pair("Toomics (KO)", "toomics.com"),
        Pair("Bomtoon (KO)", "bomtoon"),
        Pair("KakaoPage (KO)", "kakao"),
        Pair("KuaiKan Manhua (CN)", "Kuaikanmanhua"),
        Pair("QQ (CN)", "ac.qq.com"),
        Pair("Dajiaochong Manhua (CN)", "dajiaochongmanhua.com"),
        Pair("Weibo Manhua (CN)", "manhua.weibo.com"),
        Pair("Manman Manhua (CN)", "manmanapp.com")
    )
}