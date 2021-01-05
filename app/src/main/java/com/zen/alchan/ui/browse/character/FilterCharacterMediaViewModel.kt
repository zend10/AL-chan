package com.zen.alchan.ui.browse.character

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.genericType
import com.zen.alchan.helper.replaceUnderscore
import type.MediaFormat
import type.MediaSort
import type.MediaType

class FilterCharacterMediaViewModel(private val gson: Gson) : ViewModel() {

    var sortBy: MediaSort? = null
    var orderByDescending: Boolean = true

    var selectedFormats: ArrayList<MediaFormat>? = null
    var showOnlyOnList: Boolean? = null

    val mediaSortMap = hashMapOf(
        Pair(MediaSort.POPULARITY, R.string.popularity),
        Pair(MediaSort.SCORE, R.string.average_score),
        Pair(MediaSort.FAVOURITES, R.string.favorites),
        Pair(MediaSort.TITLE_ROMAJI, R.string.title),
        Pair(MediaSort.START_DATE, R.string.start_date)
    )

    val orderByList = listOf(R.string.ascending, R.string.descending)

    private val mediaFormatList: List<MediaFormat>
        get() {
            val list = ArrayList(Constant.ANIME_FORMAT_LIST)
            list.addAll(Constant.MANGA_FORMAT_LIST)
            return list
        }

    fun deserializeSelectedFormats(selectedFormatString: String?): ArrayList<MediaFormat>? {
        if (selectedFormatString == null) {
            return null
        }

        return gson.fromJson(selectedFormatString, genericType<List<MediaFormat>>())
    }

    fun getMediaFormatArrayPair(): Pair<Array<String>, BooleanArray> {
        val stringArray = mediaFormatList.map { it.name.replaceUnderscore() }.toTypedArray()
        val booleanArray = BooleanArray(stringArray.size)
        selectedFormats?.forEach {
            val selectedIndex = mediaFormatList.indexOf(it)
            if (selectedIndex != -1) {
                booleanArray[selectedIndex] = true
            }
        }
        return Pair(stringArray, booleanArray)
    }

    fun passMediaFormatFilterValue(index: Int, isChecked: Boolean) {
        if (isChecked) {
            if (selectedFormats == null) {
                selectedFormats = ArrayList()
            }
            selectedFormats?.add(mediaFormatList[index])
        } else {
            selectedFormats?.remove(mediaFormatList[index])
        }
    }
}