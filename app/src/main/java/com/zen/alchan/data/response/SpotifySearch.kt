package com.zen.alchan.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SpotifySearch(
    @SerializedName("tracks")
    @Expose
    val tracks: SpotifyTrack
)

class SpotifyTrack(
    @SerializedName("items")
    @Expose
    val items: List<SpotifyTrackItem>
)

class SpotifyTrackItem(
    @SerializedName("external_urls")
    @Expose
    val externalUrls: SpotifyTrackItemExternalUrl
)

class SpotifyTrackItemExternalUrl(
    @SerializedName("spotify")
    @Expose
    val spotify: String
)