package com.zen.alchan.helper

import com.zen.alchan.BuildConfig

object Constant {
    const val ANILIST_URL = "https://anilist.co/"
    const val ANILIST_REGISTER_URL = "${ANILIST_URL}signup"
    const val ANILIST_LOGIN_URL = "${ANILIST_URL}login"

    const val SHARED_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + ".LocalStorage"
}