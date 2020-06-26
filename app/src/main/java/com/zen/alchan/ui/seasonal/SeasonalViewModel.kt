package com.zen.alchan.ui.seasonal

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.SearchRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.SeasonalAnime
import com.zen.alchan.helper.enums.SeasonalCategory
import type.*

class SeasonalViewModel(private val searchRepository: SearchRepository,
                        private val mediaListRepository: MediaListRepository,
                        private val userRepository: UserRepository,
                        val gson: Gson
) : ViewModel() {

    var selectedYear: Int? = null
    var selectedSeason: MediaSeason? = null
    var selectedStatus: MediaStatus? = null
    var selectedSort: MediaSort? = null
    var selectedIsAdult: Boolean? = null
    var selectedOnList: Boolean? = null

    val showAdultContent: Boolean
        get() = userRepository.currentUser?.options?.displayAdultContent ?: false

    val mediaSortArray = arrayOf(
        "NEWEST",
        "OLDEST",
        "TITLE ROMAJI",
        "TITLE ENGLISH",
        "TITLE NATIVE",
        "HIGHEST SCORE",
        "LOWEST SCORE",
        "MOST POPULAR",
        "LEAST POPULAR",
        "MOST FAVORITE",
        "LEAST FAVORITE",
        "TRENDING"
    )

    var mediaSortList = arrayListOf(
        MediaSort.START_DATE_DESC,
        MediaSort.START_DATE,
        MediaSort.TITLE_ROMAJI,
        MediaSort.TITLE_ENGLISH,
        MediaSort.TITLE_NATIVE,
        MediaSort.SCORE_DESC,
        MediaSort.SCORE,
        MediaSort.POPULARITY_DESC,
        MediaSort.POPULARITY,
        MediaSort.FAVOURITES_DESC,
        MediaSort.FAVOURITES,
        MediaSort.TRENDING_DESC
    )

    var tvPage = 1
    var tvHasNextPage = true
    var tvIsInit = false
    var tvList = ArrayList<SeasonalAnime>()

    var tvShortPage = 1
    var tvShortHasNextPage = true
    var tvShortIsInit = false
    var tvShortList = ArrayList<SeasonalAnime>()

    var moviePage = 1
    var movieHasNextPage = true
    var movieIsInit = false
    var movieList = ArrayList<SeasonalAnime>()

    var othersPage = 1
    var othersHasNextPage = true
    var othersIsInit = false
    var othersList = ArrayList<SeasonalAnime>()

    val seasonalAnimeTvResponse by lazy {
        searchRepository.seasonalAnimeTvResponse
    }

    val seasonalAnimeTvShortResponse by lazy {
        searchRepository.seasonalAnimeTvShortResponse
    }

    val seasonalAnimeMovieResponse by lazy {
        searchRepository.seasonalAnimeMovieResponse
    }

    val seasonalAnimeOthersResponse by lazy {
        searchRepository.seasonalAnimeOthersResponse
    }

    val seasonalAnimeTvData by lazy {
        searchRepository.seasonalAnimeTvData
    }

    val seasonalAnimeTvShortData by lazy {
        searchRepository.seasonalAnimeTvShortData
    }

    val seasonalAnimeMovieData by lazy {
        searchRepository.seasonalAnimeMovieData
    }

    val seasonalAnimeOthersData by lazy {
        searchRepository.seasonalAnimeOthersData
    }

    val addAnimeToPlanningResponse by lazy {
        mediaListRepository.addAnimeToPlanningResponse
    }

    fun getSeasonalAnime(seasonalCategory: SeasonalCategory) {
        val page = when (seasonalCategory) {
            SeasonalCategory.TV -> tvPage
            SeasonalCategory.TV_SHORT -> tvShortPage
            SeasonalCategory.MOVIE -> moviePage
            SeasonalCategory.OTHERS -> othersPage
        }

        val season = if (selectedStatus == null) selectedSeason else null
        val year = if (selectedStatus == null) selectedYear else null

        searchRepository.getSeasonalAnime(
            page, season, year, selectedStatus, seasonalCategory, selectedIsAdult!!, selectedOnList, listOf(selectedSort!!)
        )
    }

    fun resetHasNextPage() {
        tvHasNextPage = true
        tvShortHasNextPage = true
        movieHasNextPage = true
        othersHasNextPage = true
    }

    fun refresh() {
        if (tvHasNextPage) {
            tvPage = 1
            tvList.clear()
            getSeasonalAnime(SeasonalCategory.TV)
        }

        if (tvShortHasNextPage) {
            tvShortPage = 1
            tvShortList.clear()
            getSeasonalAnime(SeasonalCategory.TV_SHORT)
        }

        if (movieHasNextPage) {
            moviePage = 1
            movieList.clear()
            getSeasonalAnime(SeasonalCategory.MOVIE)
        }

        if (othersHasNextPage) {
            othersPage = 1
            othersList.clear()
            getSeasonalAnime(SeasonalCategory.OTHERS)
        }
    }

    fun addToPlanning(id: Int) {
        mediaListRepository.addAnimeToPlanning(id)
    }

    fun updateList(updateData: AnimeListEntryMutation.Data) {
        val mediaList = Converter.convertMediaList(updateData.saveMediaListEntry!!)
        val format = updateData.saveMediaListEntry.media?.format
        when (format) {
            MediaFormat.TV -> {
                val index = tvList.indexOf(tvList.find { it.id == mediaList.media?.id })
                tvList[index].mediaListEntry = mediaList
            }
            MediaFormat.TV_SHORT -> {
                val index = tvShortList.indexOf(tvShortList.find { it.id == mediaList.media?.id })
                tvShortList[index].mediaListEntry = mediaList
            }
            MediaFormat.MOVIE -> {
                val index = movieList.indexOf(movieList.find { it.id == mediaList.media?.id })
                movieList[index].mediaListEntry = mediaList
            }
            MediaFormat.OVA, MediaFormat.ONA, MediaFormat.SPECIAL -> {
                val index = othersList.indexOf(othersList.find { it.id == mediaList.media?.id })
                othersList[index].mediaListEntry = mediaList
            }
        }
    }
}