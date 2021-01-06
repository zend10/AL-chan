package com.zen.alchan.ui.browse.staff.voice

import androidx.lifecycle.ViewModel
import com.zen.alchan.R
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.helper.pojo.StaffCharacter
import type.MediaSort

class StaffVoiceViewModel(private val browseRepository: BrowseRepository) : ViewModel() {

    var staffId: Int? = null
    var page = 1
    var hasNextPage = true

    var isInit = false
    var staffCharacters = ArrayList<StaffCharacter?>()

    var sortBy: MediaSort? = null
    var onlyShowOnList: Boolean = false

    val mediaSortArray = arrayOf(
        R.string.characters,
        R.string.newest,
        R.string.oldest,
        R.string.title_romaji,
        R.string.title_english,
        R.string.title_native,
        R.string.highest_score,
        R.string.lowest_score,
        R.string.most_popular,
        R.string.least_popular,
        R.string.most_favorite,
        R.string.least_favorite
    )

    var mediaSortList = arrayListOf(
        null,
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
        MediaSort.FAVOURITES
    )

    val staffCharacterData by lazy {
        browseRepository.staffCharacterData
    }

    val staffMediaCharacterData by lazy {
        browseRepository.staffMediaCharacterData
    }

    fun getStaffCharacters(getFromBeginning: Boolean = false) {
        if (getFromBeginning) {
            page = 1
            hasNextPage = true
            staffCharacters.clear()
        }

        if (hasNextPage && staffId != null) {
            if (sortBy == null) {
                browseRepository.getStaffCharacter(staffId!!, page)
            } else {
                browseRepository.getStaffMediaCharacter(staffId!!, page, sortBy!!, if (onlyShowOnList) true else null)
            }
        }
    }
}