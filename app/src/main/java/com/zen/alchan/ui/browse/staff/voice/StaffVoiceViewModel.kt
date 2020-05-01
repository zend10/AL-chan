package com.zen.alchan.ui.browse.staff.voice

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.helper.pojo.StaffCharacter

class StaffVoiceViewModel(private val browseRepository: BrowseRepository) : ViewModel() {

    var staffId: Int? = null
    var page = 1
    var hasNextPage = true

    var isInit = false
    var staffCharacters = ArrayList<StaffCharacter?>()

    val staffCharacterData by lazy {
        browseRepository.staffCharacterData
    }

    fun getStaffCharacters() {
        if (hasNextPage && staffId != null) browseRepository.getStaffCharacter(staffId!!, page)
    }
}