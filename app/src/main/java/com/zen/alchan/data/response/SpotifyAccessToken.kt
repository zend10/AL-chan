package com.zen.alchan.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SpotifyAccessToken(
    val accessToken: String = "",
    val expiresIn: Int = 0
)
