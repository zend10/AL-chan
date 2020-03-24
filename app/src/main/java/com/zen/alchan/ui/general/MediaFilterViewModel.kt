package com.zen.alchan.ui.general

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.CountryCode
import com.zen.alchan.helper.enums.MediaListSort
import com.zen.alchan.helper.pojo.MediaFilteredData
import com.zen.alchan.helper.replaceUnderscore
import io.reactivex.CompletableOnSubscribe
import type.*
import java.sql.Struct

class MediaFilterViewModel(private val userRepository: UserRepository,
                           private val mediaRepository: MediaRepository,
                           val gson: Gson
) : ViewModel() {

    lateinit var mediaType: MediaType
    var isHandleSearch = false

    var filteredData: MediaFilteredData? = null
    var currentData = MediaFilteredData()

    val defaultSort: MediaListSort
        get() = when(userRepository.viewerData.value?.mediaListOptions?.rowOrder) {
            "title" -> MediaListSort.TITLE
            "score" -> MediaListSort.SCORE
            "updatedAt" -> MediaListSort.LAST_UPDATED
            "id" -> MediaListSort.LAST_ADDED
            else -> MediaListSort.TITLE
        }

    val mediaFormatList: List<MediaFormat?>
        get() {
            val formatList = when (mediaType) {
                MediaType.ANIME -> ArrayList(Constant.ANIME_FORMAT_LIST)
                MediaType.MANGA -> ArrayList(Constant.MANGA_FORMAT_LIST)
                else -> ArrayList()
            }
            formatList.add(0, null)
            return formatList
        }

    val mediaSeasonList: List<MediaSeason?>
        get() {
            val seasonList = ArrayList<MediaSeason?>(Constant.SEASON_LIST)
            seasonList.add(0, null)
            return seasonList
        }

    val mediaCountryList: List<CountryCode?>
        get() {
            val countryList = ArrayList<CountryCode?>()
            countryList.add(null)
            countryList.addAll(CountryCode.values())
            return countryList
        }

    val mediaStatusList: List<MediaStatus?>
        get() {
            val statusList = ArrayList<MediaStatus?>(Constant.MEDIA_STATUS_LIST)
            statusList.add(0, null)
            return statusList
        }

    val mediaSourceList: List<MediaSource?>
        get() {
            val sourceList = when (mediaType) {
                MediaType.ANIME -> ArrayList(Constant.ANIME_SOURCE_LIST)
                MediaType.MANGA -> ArrayList(Constant.MANGA_SOURCE_LIST)
                else -> ArrayList()
            }
            sourceList.add(0, null)
            return sourceList
        }

    val mediaSortList = MediaListSort.values().toList()

    val genreList: List<String>
        get() = mediaRepository.genreList

    fun getMediaFormatStringArray(): Array<String> {
        val stringList = ArrayList<String>()
        mediaFormatList.forEach {
            stringList.add(it?.name?.replaceUnderscore() ?: "-")
        }
        return stringList.toTypedArray()
    }

    fun getMediaSeasonStringArray(): Array<String> {
        val stringList = ArrayList<String>()
        mediaSeasonList.forEach {
            stringList.add(it?.name?.replaceUnderscore() ?: "-")
        }
        return stringList.toTypedArray()
    }

    fun getMediaCountryStringArray(): Array<String> {
        val stringList = ArrayList<String>()
        mediaCountryList.forEach {
            stringList.add(it?.value?.replaceUnderscore() ?: "-")
        }
        return stringList.toTypedArray()
    }

    fun getMediaStatusStringArray(): Array<String> {
        val stringList = ArrayList<String>()
        mediaStatusList.forEach {
            stringList.add(it?.name?.replaceUnderscore() ?: "-")
        }
        return stringList.toTypedArray()
    }

    fun getMediaSourceStringArray(): Array<String> {
        val stringList = ArrayList<String>()
        mediaSourceList.forEach {
            stringList.add(it?.name?.replaceUnderscore() ?: "-")
        }
        return stringList.toTypedArray()
    }

    fun getMediaListSortStringArray(): Array<String> {
        val stringList = ArrayList<String>()
        mediaSortList.forEach {
            stringList.add(it.name.replaceUnderscore())
        }
        return stringList.toTypedArray()
    }

    fun getGenreListStringArray(): Array<String> {
        return genreList.toTypedArray()
    }
}