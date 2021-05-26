package com.zen.alchan.helper

import com.zen.alchan.BuildConfig

object Constant {
    const val ANILIST_API_BASE_URL = "https://graphql.anilist.co"
    const val ANILIST_API_VERSION = 2
    const val ANILIST_WEBSITE_URL = "https://anilist.co"

    private const val ANILIST_CLIENT_ID = 1988
    const val ANILIST_LOGIN_URL = "$ANILIST_WEBSITE_URL/api/v2/oauth/authorize?client_id=$ANILIST_CLIENT_ID&response_type=token"
    const val ANILIST_REGISTER_URL = "$ANILIST_WEBSITE_URL/signup"

    const val SHARED_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + ".LocalStorage"
}