package com.zen.alchan.data.datasource

import com.zen.alchan.data.response.Announcement
import com.zen.alchan.data.response.SpotifyAccessToken
import com.zen.alchan.data.response.SpotifySearch
import com.zen.alchan.data.response.YouTubeSearch
import retrofit2.Call

interface InfoDataSource {
    fun getAnnouncement(): Call<Announcement>
    fun getYouTubeVideo(key: String, query: String): Call<YouTubeSearch>
    fun getSpotifyAccessToken(): Call<SpotifyAccessToken>
    fun getSpotifyTrack(query: String): Call<SpotifySearch>
}