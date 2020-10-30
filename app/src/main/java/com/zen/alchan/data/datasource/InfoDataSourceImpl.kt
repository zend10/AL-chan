package com.zen.alchan.data.datasource

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zen.alchan.BuildConfig
import com.zen.alchan.data.network.GithubRestService
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.network.YouTubeRestService
import com.zen.alchan.data.response.Announcement
import com.zen.alchan.data.response.YouTubeSearch
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.utils.AndroidUtility
import retrofit2.Call

class InfoDataSourceImpl(private val githubRestService: GithubRestService,
                         private val youTubeRestService: YouTubeRestService
) : InfoDataSource {

    override fun getAnnouncement(): Call<Announcement> {
        return githubRestService.getAnnouncement()
    }

    override fun getYouTubeVideo(key: String, query: String): Call<YouTubeSearch> {
        return youTubeRestService.searchVideo(key, "snippet", query, "video", 1)
    }
}