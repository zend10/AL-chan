package com.zen.alchan.data.datasource

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.GithubRestService
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.Announcement
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.utils.AndroidUtility
import retrofit2.Call

class InfoDataSourceImpl(private val githubRestService: GithubRestService) : InfoDataSource {

    override fun getAnnouncement(): Call<Announcement> {
        return githubRestService.getAnnouncement()
    }
}