package com.zen.alchan.helper

import com.zen.alchan.BuildConfig

object Constant {
    const val ANILIST_API_BASE_URL = "https://graphql.anilist.co"
    const val ANILIST_API_STATUS_VERSION = 2
    const val ANILIST_API_SOURCE_VERSION = 3
    const val ANILIST_API_RELATION_TYPE_VERSION = 2
    const val ANILIST_WEBSITE_URL = "https://anilist.co"

    private const val ANILIST_CLIENT_ID = 1988
    const val ANILIST_LOGIN_URL = "$ANILIST_WEBSITE_URL/api/v2/oauth/authorize?client_id=$ANILIST_CLIENT_ID&response_type=token"
    const val ANILIST_REGISTER_URL = "$ANILIST_WEBSITE_URL/signup"
    const val ANILIST_PROFILE_SETTINGS_URL = "$ANILIST_WEBSITE_URL/settings"
    const val ANILIST_ACCOUNT_SETTINGS_URL = "$ANILIST_PROFILE_SETTINGS_URL/account"
    const val ANILIST_LISTS_SETTINGS_URL = "$ANILIST_PROFILE_SETTINGS_URL/lists"
    const val ANILIST_IMPORT_LISTS_URL = "$ANILIST_PROFILE_SETTINGS_URL/import"
    const val ANILIST_CONNECT_WITH_TWITTER_URL = "$ANILIST_PROFILE_SETTINGS_URL/twitter"
    const val ANILIST_ACTIVITY = "$ANILIST_WEBSITE_URL/activity/"

    const val ALCHAN_FORUM_THREAD_URL = "${ANILIST_WEBSITE_URL}/forum/thread/12889"
    const val ALCHAN_GITHUB_URL = "https://github.com/zend10/AL-chan"
    const val ALCHAN_EMAIL_ADDRESS = "alchanapp@gmail.com"
    const val ALCHAN_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
    const val ALCHAN_TWITTER_URL = "https://twitter.com/alchan_app"
    const val ALCHAN_PRIVACY_POLICY_URL = "https://zend10.github.io/AL-chan/privacy.html"

    const val ALCHAN_RAW_GITHUB_URL = "https://raw.githubusercontent.com/zend10/AL-chan/master/"
    const val ALCHAN_VIDEO_THUMBNAIL_URL = "${ALCHAN_RAW_GITHUB_URL}docs/images/video_thumbnail.png"
    const val ALCHAN_YOUTUBE_THUMBNAIL_URL = "${ALCHAN_RAW_GITHUB_URL}docs/images/youtube_thumbnail.png"

    const val SHARED_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + ".LocalStorage"

    const val JIKAN_API_URL = "https://api.jikan.moe/v4/"
    const val ANIME_THEMES_API_URL = "https://api.animethemes.moe/"
    const val YOUTUBE_SEARCH_API_URL = "https://www.googleapis.com/youtube/v3/"
    const val YOUTUBE_URL = "https://www.youtube.com/watch?v="
    const val SPOTIFY_AUTH_API_URL = "https://accounts.spotify.com/api/"
    const val SPOTIFY_API_URL = "https://api.spotify.com/v1/"
}