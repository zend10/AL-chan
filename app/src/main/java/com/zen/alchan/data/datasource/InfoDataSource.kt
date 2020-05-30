package com.zen.alchan.data.datasource

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.Announcement
import retrofit2.Call

interface InfoDataSource {
    fun getAnnouncement(): Call<Announcement>
}