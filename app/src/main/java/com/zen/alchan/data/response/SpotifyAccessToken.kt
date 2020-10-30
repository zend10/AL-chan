package com.zen.alchan.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SpotifyAccessToken(
    @SerializedName("access_token")
    @Expose
    val accessToken: String,
    @SerializedName("expires_in")
    @Expose
    val expiresIn: Int
)