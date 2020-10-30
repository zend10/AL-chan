package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zen.alchan.data.datasource.InfoDataSource
import com.zen.alchan.data.localstorage.InfoManager
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.Announcement
import com.zen.alchan.data.response.YouTubeSearch
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.utils.AndroidUtility

class InfoRepositoryImpl(private val infoDataSource: InfoDataSource,
                         private val infoManager: InfoManager) : InfoRepository {

    private val _announcementResponse = SingleLiveEvent<Resource<Announcement>>()
    override val announcementResponse: LiveData<Resource<Announcement>>
        get() = _announcementResponse

    override val lastAnnouncementId: Int?
        get() = infoManager.lastAnnouncementId

    private val _youTubeVideoResponse = SingleLiveEvent<Resource<YouTubeSearch>>()
    override val youTubeVideoResponse: LiveData<Resource<YouTubeSearch>>
        get() = _youTubeVideoResponse

    private var youtubeKey: String? = null

    override fun getAnnouncement() {
        infoDataSource.getAnnouncement().enqueue(AndroidUtility.apiCallback(_announcementResponse))
    }

    override fun setLastAnnouncementId(value: Int) {
        infoManager.setLastAnnouncementId(value)
    }

    override fun getYouTubeVideo(query: String) {
        _youTubeVideoResponse.postValue(Resource.Loading())

        if (youtubeKey == null) {
            val ref = FirebaseDatabase.getInstance().getReference("keys")
            ref.child("youtube").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    youtubeKey = snapshot.value as String
                    infoDataSource.getYouTubeVideo(youtubeKey ?: "", query).enqueue(AndroidUtility.apiCallback(_youTubeVideoResponse))
                }

                override fun onCancelled(error: DatabaseError) {
                    _youTubeVideoResponse.postValue(Resource.Error(error.message))
                }
            })
        } else {
            infoDataSource.getYouTubeVideo(youtubeKey ?: "", query).enqueue(AndroidUtility.apiCallback(_youTubeVideoResponse))
        }
    }
}