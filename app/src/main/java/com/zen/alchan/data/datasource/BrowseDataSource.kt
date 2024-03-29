package com.zen.alchan.data.datasource

import com.apollographql.apollo3.api.ApolloResponse
import com.zen.alchan.*
import com.zen.alchan.data.response.animethemes.AnimePaginationResponse
import com.zen.alchan.data.response.mal.AnimeResponse
import com.zen.alchan.data.response.mal.MangaResponse
import com.zen.alchan.data.response.spotify.SpotifyAccessTokenResponse
import com.zen.alchan.data.response.spotify.TrackSearchResponse
import com.zen.alchan.data.response.youtube.VideoSearchResponse
import com.zen.alchan.type.*
import io.reactivex.rxjava3.core.Observable

interface BrowseDataSource {
    fun getUserQuery(id: Int?, name: String?, sort: List<UserStatisticsSort>): Observable<ApolloResponse<UserQuery.Data>>
    fun getMediaQuery(id: Int): Observable<ApolloResponse<MediaQuery.Data>>
    fun getMediaCharactersQuery(id: Int, page: Int, language: StaffLanguage): Observable<ApolloResponse<MediaCharactersQuery.Data>>
    fun getMediaStaffQuery(id: Int, page: Int): Observable<ApolloResponse<MediaStaffQuery.Data>>
    fun getMediaFollowingMediaListQuery(id: Int, page: Int): Observable<ApolloResponse<MediaFollowingMediaListQuery.Data>>
    fun getMediaActivityQuery(id: Int, page: Int): Observable<ApolloResponse<MediaActivityQuery.Data>>
    fun getCharacterQuery(id: Int, page: Int, sort: List<MediaSort>, type: MediaType?, onList: Boolean?): Observable<ApolloResponse<CharacterQuery.Data>>
    fun getStaffQuery(
        id: Int,
        page: Int,
        staffMediaSort: List<MediaSort>,
        characterSort: List<CharacterSort>,
        characterMediaSort: List<MediaSort>,
        onList: Boolean?
    ): Observable<ApolloResponse<StaffQuery.Data>>
    fun getStudioQuery(id: Int, page: Int, sort: List<MediaSort>, onList: Boolean?): Observable<ApolloResponse<StudioQuery.Data>>

    fun getMangaDetails(malId: Int): Observable<MangaResponse>
    fun getAnimeDetailsFromMal(malId: Int): Observable<AnimeResponse>
    fun getAnimeDetailsFromAnimeThemes(malId: Int): Observable<AnimePaginationResponse>
    fun getYouTubeVideo(key: String, searchQuery: String): Observable<VideoSearchResponse>
    fun getSpotifyAccessToken(): Observable<SpotifyAccessTokenResponse>
    fun getSpotifyTrack(searchQuery: String): Observable<TrackSearchResponse>
}