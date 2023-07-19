package com.zen.alchan.data.response.spotify

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SpotifyAccessTokenResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("expires_in")
    val expiresIn: Int? = null
)
