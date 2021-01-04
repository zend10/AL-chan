package com.zen.alchan.ui.browse.character

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.R
import com.zen.alchan.helper.genericType
import type.MediaFormat
import type.MediaSort

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

    fun deserializeSelectedFormats(selectedFormatString: String?): ArrayList<MediaFormat>? {
        if (selectedFormatString == null) {
            return null
        }

        return gson.fromJson(selectedFormatString, genericType<List<MediaFormat>>())
    }
}