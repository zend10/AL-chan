package com.zen.alchan.data.converter

import com.zen.alchan.data.response.SpotifyAccessToken
import com.zen.alchan.data.response.spotify.SpotifyAccessTokenResponse

fun SpotifyAccessTokenResponse.convert(): SpotifyAccessToken {
    return SpotifyAccessToken(
        accessToken = accessToken ?: "",
        expiresIn = expiresIn ?: 0
    )
}