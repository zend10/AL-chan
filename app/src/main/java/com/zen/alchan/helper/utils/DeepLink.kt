package com.zen.alchan.helper.utils

import android.net.Uri

class DeepLink(
    val uri: Uri?
) {
    private fun getAuthority(): String? {
        return uri?.authority
    }

    private fun getFirstPath(): String? {
        return uri?.pathSegments?.firstOrNull()
    }

    fun isLogin() = getAuthority() == LOGIN
    fun isHome() = getAuthority() == HOME
    fun isAnimeList() = getAuthority() == ANIME_LIST
    fun isMangaList() = getAuthority() == MANGA_LIST

    fun isAppSettings() = getAuthority() == SETTINGS && getFirstPath() == APP_SETTINGS

    fun isSpoiler() = getAuthority() == SPOILER

    fun getQueryParamOfOrNull(key: String): String? {
        return uri?.getQueryParameter(key)
    }

    companion object {
        fun create(uri: Uri?): DeepLink {
            return DeepLink(uri)
        }

        private const val SCHEME = "alchan"

        private const val LOGIN = "login"
        private const val HOME = "home"
        private const val ANIME_LIST = "animelist"
        private const val MANGA_LIST = "mangalist"

        private const val SETTINGS = "settings"
        private const val APP_SETTINGS = "app"

        private const val SPOILER = "spoiler"

        fun generateAppSettings() = generateDeepLink(SETTINGS, APP_SETTINGS)

        private fun generateDeepLink(vararg paths: String): DeepLink {
            return create(Uri.parse("${SCHEME}://${paths.joinToString("/")}"))
        }
    }
}