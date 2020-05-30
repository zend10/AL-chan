package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.Announcement

interface InfoRepository {
    val announcementResponse: LiveData<Resource<Announcement>>
    val lastAnnouncementId: Int?
    fun getAnnouncement()
    fun setLastAnnouncementId(value: Int)
}