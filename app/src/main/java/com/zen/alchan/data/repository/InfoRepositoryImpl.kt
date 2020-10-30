package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zen.alchan.data.datasource.InfoDataSource
import com.zen.alchan.data.localstorage.InfoManager
import com.zen.alchan.data.localstorage.TempStorageManager
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.Announcement
import com.zen.alchan.data.response.SpotifyAccessToken
import com.zen.alchan.data.response.SpotifySearch
import com.zen.alchan.data.response.YouTubeSearch
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoRepositoryImpl(private val infoDataSource: InfoDataSource,
                         private val infoManager: InfoManager,
                         private val tempStorageManager: TempStorageManager) : InfoRepository {

    private val _announcementResponse = SingleLiveEvent<Resource<Announcement>>()
    override val announcementResponse: LiveData<Resource<Announcement>>
        get() = _announcementResponse

    override val lastAnnouncementId: Int?
        get() = infoManager.lastAnnouncementId

    private val _youTubeVideoResponse = SingleLiveEvent<Resource<YouTubeSearch>>()
    override val youTubeVideoResponse: LiveData<Resource<YouTubeSearch>>
        get() = _youTubeVideoResponse

    private val _spotifyTrackResponse = SingleLiveEvent<Resource<SpotifySearch>>()
    override val spotifyTrackResponse: LiveData<Resource<SpotifySearch>>
        get() = _spotifyTrackResponse

    override fun getAnnouncement() {
        infoDataSource.getAnnouncement().enqueue(AndroidUtility.apiCallback(_announcementResponse))
    }

    override fun setLastAnnouncementId(value: Int) {
        infoManager.setLastAnnouncementId(value)
    }

    override fun getYouTubeVideo(query: String) {
        _youTubeVideoResponse.postValue(Resource.Loading())

        if (tempStorageManager.youtubeKey == null) {
            val ref = FirebaseDatabase.getInstance().getReference("keys")
            ref.child("youtube").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tempStorageManager.youtubeKey = snapshot.value as String
                    infoDataSource.getYouTubeVideo(tempStorageManager.youtubeKey ?: "", query).enqueue(AndroidUtility.apiCallback(_youTubeVideoResponse))
                }

                override fun onCancelled(error: DatabaseError) {
                    _youTubeVideoResponse.postValue(Resource.Error(error.message))
                }
            })
        } else {
            infoDataSource.getYouTubeVideo(tempStorageManager.youtubeKey ?: "", query).enqueue(AndroidUtility.apiCallback(_youTubeVideoResponse))
        }
    }

    override fun getSpotifyTrack(query: String) {
        _spotifyTrackResponse.postValue(Resource.Loading())

        if (tempStorageManager.spotifyAccessToken == null || Utility.getCurrentTimestamp() > (tempStorageManager.spotifyAccessTokenLastRetrieve ?: 0) + 3600000) {
            val ref = FirebaseDatabase.getInstance().getReference("keys")
            ref.child("spotify").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tempStorageManager.spotifyKey = snapshot.value as String

                    infoDataSource.getSpotifyAccessToken().enqueue(object : Callback<SpotifyAccessToken> {
                        override fun onResponse(
                            call: Call<SpotifyAccessToken>,
                            response: Response<SpotifyAccessToken>
                        ) {
                            if (response.isSuccessful) {
                                val accessToken = response.body()
                                tempStorageManager.spotifyAccessTokenLastRetrieve = Utility.getCurrentTimestamp()
                                tempStorageManager.spotifyAccessToken = accessToken?.accessToken
                                tempStorageManager.spotifyAccessTokenExpiresIn = accessToken?.expiresIn
                                infoDataSource.getSpotifyTrack(query).enqueue(AndroidUtility.apiCallback(_spotifyTrackResponse))
                            } else {
                                _spotifyTrackResponse.postValue(Resource.Error(response.errorBody()?.string() ?: ""))
                            }
                        }

                        override fun onFailure(call: Call<SpotifyAccessToken>, t: Throwable) {
                            _spotifyTrackResponse.postValue(Resource.Error(t.localizedMessage ?: ""))
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    _spotifyTrackResponse.postValue(Resource.Error(error.message))
                }
            })
        } else {
            infoDataSource.getSpotifyTrack(query).enqueue(AndroidUtility.apiCallback(_spotifyTrackResponse))
        }
    }
}