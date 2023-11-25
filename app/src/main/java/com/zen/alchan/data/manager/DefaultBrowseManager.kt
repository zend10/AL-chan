package com.zen.alchan.data.manager

import com.zen.alchan.BuildConfig
import com.zen.alchan.data.localstorage.SharedPreferencesHandler
import com.zen.alchan.data.response.SpotifyAccessToken
import com.zen.alchan.helper.enums.ListType

class DefaultBrowseManager(private val sharedPreferencesHandler: SharedPreferencesHandler) : BrowseManager {

    override var othersListType: ListType
        get() = sharedPreferencesHandler.othersListType ?: ListType.LINEAR
        set(value) { sharedPreferencesHandler.othersListType = value }

    override val youTubeApiKey: String
        get() = BuildConfig.YOUTUBE_API_KEY

    override val spotifyApiKey: String
        get() = BuildConfig.SPOTIFY_API_KEY

    override var spotifyAccessToken: SpotifyAccessToken
        get() = sharedPreferencesHandler.spotifyAccessToken ?: SpotifyAccessToken()
        set(value) { sharedPreferencesHandler.spotifyAccessToken = value }

    override var spotifyAccessTokenLastRetrieve: Long
        get() = sharedPreferencesHandler.spotifyAccessTokenLastRetrieve ?: 0
        set(value) { sharedPreferencesHandler.spotifyAccessTokenLastRetrieve = value }
}