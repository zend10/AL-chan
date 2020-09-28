package com.zen.alchan.ui.common.filter

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.R
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.MediaTagCollection
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.CountryCode
import com.zen.alchan.helper.enums.MediaListSort
import com.zen.alchan.helper.pojo.FilterRange
import com.zen.alchan.helper.pojo.MediaFilterData
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.utils.Utility
import type.*

class MediaFilterViewModel(private val userRepository: UserRepository,
                           private val mediaRepository: MediaRepository,
                           val gson: Gson
) : ViewModel() {

    lateinit var mediaType: MediaType
    var isExplore = false
    var filterData: MediaFilterData? = null
    var currentData = MediaFilterData()

    val defaultSort: MediaListSort
        get() = when (userRepository.currentUser?.mediaListOptions?.rowOrder) {
            "title" -> MediaListSort.TITLE
            "score" -> MediaListSort.SCORE
            "updatedAt" -> MediaListSort.LAST_UPDATED
            "id" -> MediaListSort.LAST_ADDED
            else -> MediaListSort.TITLE
        }

    val defaultOrderByDescending: Boolean
        get() = when (userRepository.currentUser?.mediaListOptions?.rowOrder) {
            "title" -> false
            "score" -> true
            "updatedAt" -> true
            "id" -> true
            else -> false
        }

    val mediaListSortList = MediaListSort.values().toList()

    var mediaSortMap = hashMapOf(
        Pair(MediaSort.START_DATE_DESC, R.string.newest),
        Pair(MediaSort.START_DATE, R.string.oldest),
        Pair(MediaSort.TITLE_ROMAJI, R.string.title_romaji),
        Pair(MediaSort.TITLE_ENGLISH, R.string.title_english),
        Pair(MediaSort.TITLE_NATIVE, R.string.title_native),
        Pair(MediaSort.ID, R.string.first_added),
        Pair(MediaSort.ID_DESC, R.string.last_added),
        Pair(MediaSort.SCORE_DESC, R.string.highest_score),
        Pair(MediaSort.SCORE, R.string.lowest_score),
        Pair(MediaSort.POPULARITY_DESC, R.string.most_popular),
        Pair(MediaSort.POPULARITY, R.string.least_popular),
        Pair(MediaSort.FAVOURITES_DESC, R.string.most_favorite),
        Pair(MediaSort.FAVOURITES, R.string.least_favorite),
        Pair(MediaSort.TRENDING_DESC, R.string.trending)
    )

    val orderByList = listOf(R.string.ascending, R.string.descending)

    private val mediaFormatList: List<MediaFormat>
        get() {
            return when (mediaType) {
                MediaType.ANIME -> ArrayList(Constant.ANIME_FORMAT_LIST)
                MediaType.MANGA -> ArrayList(Constant.MANGA_FORMAT_LIST)
                else -> ArrayList()
            }
        }

    private val mediaStatusList: List<MediaStatus>
        get() = Constant.MEDIA_STATUS_LIST

    private val mediaSourceList: List<MediaSource>
        get() {
            return when (mediaType) {
                MediaType.ANIME -> ArrayList(Constant.ANIME_SOURCE_LIST)
                MediaType.MANGA -> ArrayList(Constant.MANGA_SOURCE_LIST)
                else -> ArrayList()
            }
        }

    val mediaCountryList: List<CountryCode?>
        get() {
            val countryList = ArrayList<CountryCode?>()
            countryList.add(null)
            countryList.addAll(CountryCode.values())
            return countryList
        }

    val mediaSeasonList: List<MediaSeason?>
        get() {
            val seasonList = ArrayList<MediaSeason?>(Constant.SEASON_LIST)
            seasonList.add(0, null)
            return seasonList
        }

    val yearSeekBarMaxValue = (Utility.getCurrentYear() + 1 - Constant.FILTER_EARLIEST_YEAR).toFloat()

    val episodeSeekBarMaxValue: Float
        get() = if (mediaType == MediaType.ANIME) 150F else 500F

    val durationSeekBarMaxValue: Float
        get() = if (mediaType == MediaType.ANIME) 170F else 50F

    val mediaLicensedList: ArrayList<Pair<String, String>>
        get() {
            return when (mediaType) {
                MediaType.ANIME -> Constant.ANIME_STREAMING_SITE
                MediaType.MANGA -> Constant.MANGA_READING_SITE
                else -> Constant.ANIME_STREAMING_SITE
            }
        }

    val genreList: List<String?>
        get() = mediaRepository.genreList

    val tagList: List<MediaTagCollection>
        get() = mediaRepository.tagList

    fun getMediaFormatArrayPair(): Pair<Array<String>, BooleanArray> {
        val stringArray = mediaFormatList.map { it.name.replaceUnderscore() }.toTypedArray()
        val booleanArray = BooleanArray(stringArray.size)
        currentData.selectedFormats?.forEach {
            val selectedIndex = mediaFormatList.indexOf(it)
            if (selectedIndex != -1) {
                booleanArray[selectedIndex] = true
            }
        }
        return Pair(stringArray, booleanArray)
    }

    fun passMediaFormatFilterValue(index: Int, isChecked: Boolean) {
        if (isChecked) {
            if (currentData.selectedFormats == null) {
                currentData.selectedFormats = ArrayList()
            }
            currentData.selectedFormats?.add(mediaFormatList[index])
        } else {
            currentData.selectedFormats?.remove(mediaFormatList[index])
        }
    }

    fun getMediaStatusArrayPair(): Pair<Array<String>, BooleanArray> {
        val stringArray = mediaStatusList.map { it.name.replaceUnderscore() }.toTypedArray()
        val booleanArray = BooleanArray(stringArray.size)
        currentData.selectedStatuses?.forEach {
            val selectedIndex = mediaStatusList.indexOf(it)
            if (selectedIndex != -1) {
                booleanArray[selectedIndex] = true
            }
        }
        return Pair(stringArray, booleanArray)
    }

    fun passMediaStatusFilterValue(index: Int, isChecked: Boolean) {
        if (isChecked) {
            if (currentData.selectedStatuses == null) {
                currentData.selectedStatuses = ArrayList()
            }
            currentData.selectedStatuses?.add(mediaStatusList[index])
        } else {
            currentData.selectedStatuses?.remove(mediaStatusList[index])
        }
    }

    fun getMediaSourceArrayPair(): Pair<Array<String>, BooleanArray> {
        val stringArray = mediaSourceList.map { it.name.replaceUnderscore() }.toTypedArray()
        val booleanArray = BooleanArray(stringArray.size)
        currentData.selectedSources?.forEach {
            val selectedIndex = mediaSourceList.indexOf(it)
            if (selectedIndex != -1) {
                booleanArray[selectedIndex] = true
            }
        }
        return Pair(stringArray, booleanArray)
    }

    fun passMediaSourceFilterValue(index: Int, isChecked: Boolean) {
        if (isChecked) {
            if (currentData.selectedSources == null) {
                currentData.selectedSources = ArrayList()
            }
            currentData.selectedSources?.add(mediaSourceList[index])
        } else {
            currentData.selectedSources?.remove(mediaSourceList[index])
        }
    }

    fun getMediaCountryStringArray(): Array<String> {
        return mediaCountryList.map { it?.value ?: "-" }.toTypedArray()
    }

    fun getMediaSeasonStringArray(): Array<String> {
        return mediaSeasonList.map { it?.rawValue ?: "-" }.toTypedArray()
    }

    fun getFilterRangeForFuzzyDateInt(minValue: Int, maxValue: Int): FilterRange {
        // 0101 is to set min value to 1st January
        // 1231 is to set max value to 31st December
        return FilterRange("${minValue}0101".toInt(), "${maxValue}1231".toInt())
    }

    fun getMediaLicensedArrayPair(): Pair<Array<String>, BooleanArray> {
        val stringArray = mediaLicensedList.map { it.second }.toTypedArray()
        val booleanArray = BooleanArray(stringArray.size)
        currentData.selectedLicensed?.forEach {
            val selectedIndex = mediaLicensedList.indexOf(mediaLicensedList.find { licensed -> licensed.first == it })
            if (selectedIndex != -1) {
                booleanArray[selectedIndex] = true
            }
        }
        return Pair(stringArray, booleanArray)
    }

    fun passMediaFormatLicensedValue(index: Int, isChecked: Boolean) {
        if (isChecked) {
            if (currentData.selectedLicensed == null) {
                currentData.selectedLicensed = ArrayList()
            }
            currentData.selectedLicensed?.add(mediaLicensedList[index].first)
        } else {
            currentData.selectedLicensed?.remove(mediaLicensedList[index].first)
        }
    }

    fun getGenreListStringArray(): Array<String?> {
        return genreList.toTypedArray()
    }

    fun getTagListStringArray(): Array<String> {
        return tagList.map { it.name }.toTypedArray()
    }
}