package com.zen.alchan.data.manager

import com.zen.alchan.data.response.SpotifyAccessToken
import com.zen.alchan.helper.enums.ListType

interface BrowseManager {
    var othersListType: ListType
    val youTubeApiKey: String
    val spotifyApiKey: String
    var spotifyAccessToken: SpotifyAccessToken
    var spotifyAccessTokenLastRetrieve: Long
}