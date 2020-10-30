package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.Announcement
import com.zen.alchan.data.response.YouTubeSearch

interface InfoRepository {
    val announcementResponse: LiveData<Resource<Announcement>>
    val lastAnnouncementId: Int?
    val youTubeVideoResponse: LiveData<Resource<YouTubeSearch>>
    fun getAnnouncement()
    fun setLastAnnouncementId(value: Int)
    fun getYouTubeVideo(query: String)
}