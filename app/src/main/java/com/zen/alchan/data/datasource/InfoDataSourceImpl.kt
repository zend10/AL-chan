package com.zen.alchan.data.datasource

import com.zen.alchan.data.network.service.GithubRestService
import com.zen.alchan.data.network.service.SpotifyAuthRestService
import com.zen.alchan.data.network.service.SpotifyRestService
import com.zen.alchan.data.network.service.YouTubeRestService
import com.zen.alchan.data.response.Announcement
import com.zen.alchan.data.response.SpotifyAccessToken
import com.zen.alchan.data.response.SpotifySearch
import com.zen.alchan.data.response.YouTubeSearch
import retrofit2.Call

class InfoDataSourceImpl(private val githubRestService: GithubRestService,
                         private val youTubeRestService: YouTubeRestService,
                         private val spotifyAuthRestService: SpotifyAuthRestService,
                         private val spotifyRestService: SpotifyRestService
) : InfoDataSource {

    override fun getAnnouncement(): Call<Announcement> {
        return githubRestService.getAnnouncement()
    }

    override fun getYouTubeVideo(key: String, query: String): Call<YouTubeSearch> {
        return youTubeRestService.searchVideo(key, "snippet", query, "video", 1)
    }

    override fun getSpotifyAccessToken(): Call<SpotifyAccessToken> {
        return spotifyAuthRestService.getAccessToken("client_credentials")
    }

    override fun getSpotifyTrack(query: String): Call<SpotifySearch> {
        return spotifyRestService.searchTrack(query, "track", 1)
    }
}