package com.zen.alchan.helper

object AniListConstant {
    const val ANILIST_CLIENT_ID = 1988
    const val ANILIST_LOGIN_URL =
        "https://anilist.co/api/v2/oauth/authorize?client_id=$ANILIST_CLIENT_ID&response_type=token"
    const val ANILIST_REGISTER_URL = "https://anilist.co/signup"
    const val ANILIST_GRAPHQL_BASE_URL = "https://graphql.anilist.co"
    const val ANILIST_STATUS_VERSION = 2
}

object LocalStorageConstant {
    const val DATASTORE_FILE_NAME = "alchan.preferences_pb"

    const val LANDING_COMPLETED = "landing_completed"
    const val ANILIST_TOKEN = "anilist_token"
    const val CURRENT_ANILIST_USER = "current_anilist_user"
}

object DeeplinkConstant {
    const val SCHEME = "alchan"
    const val LOGIN = "$SCHEME://login"
}