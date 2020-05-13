package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaTagCollection
import com.zen.alchan.data.response.SeasonalAnime
import com.zen.alchan.helper.enums.SeasonalCategory
import com.zen.alchan.helper.pojo.MediaCharacters
import type.MediaFormat
import type.MediaSeason
import type.MediaSort

interface MediaRepository {
    val genreList: List<String?>
    val genreListLastRetrieved: Long?

    val tagList: List<MediaTagCollection>
    val tagListLastRetrieved: Long?

    val mediaData: LiveData<Resource<MediaQuery.Data>>
    val mediaStatus: LiveData<Resource<MediaStatusQuery.Data>>
    val mediaOverviewData: LiveData<Resource<MediaOverviewQuery.Data>>
    val mediaCharactersData: LiveData<Resource<MediaCharactersQuery.Data>>
    val mediaStaffsData: LiveData<Resource<MediaStaffsQuery.Data>>

    val trendingAnimeData: LiveData<Resource<TrendingMediaQuery.Data>>
    val trendingMangaData: LiveData<Resource<TrendingMediaQuery.Data>>
    val releasingTodayData: LiveData<Resource<ReleasingTodayQuery.Data>>

    fun getGenre()
    fun getTag()
    fun getMedia(id: Int)
    fun checkMediaStatus(mediaId: Int)
    fun getMediaOverview(id: Int)
    fun getMediaCharacters(id: Int, page: Int)
    fun getMediaStaffs(id: Int, page: Int)

    fun getTrendingAnime()
    fun getTrendingManga()
    fun getReleasingToday(page: Int)
}