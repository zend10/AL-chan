package com.zen.alchan.data.datasource

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.Announcement
import com.zen.alchan.data.response.YouTubeSearch
import retrofit2.Call

interface InfoDataSource {
    fun getAnnouncement(): Call<Announcement>
    fun getYouTubeVideo(key: String, query: String): Call<YouTubeSearch>
}