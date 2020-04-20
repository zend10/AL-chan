package com.zen.alchan.ui.browse.staff

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.pojo.StaffCharacter
import com.zen.alchan.helper.pojo.StaffMedia

class StaffViewModel(private val browseRepository: BrowseRepository,
                     private val userRepository: UserRepository
) : ViewModel() {

    var staffId: Int? = null
    var currentStaffData: StaffQuery.Staff? = null

    var characterPage = 1
    var characterHasNextPage = true

    var mediaPage = 1
    var mediaHasNextPage = true

    var isCharacterInit = false
    var isMediaInit = false

    var staffCharacterList = ArrayList<StaffCharacter>()
    var staffMediaList = ArrayList<StaffMedia>()

    val staffData by lazy {
        browseRepository.staffData
    }

    val staffCharacterData by lazy {
        browseRepository.staffCharacterData
    }

    val staffMediaData by lazy {
        browseRepository.staffMediaData
    }

    val staffIsFavoriteData by lazy {
        browseRepository.staffIsFavoriteData
    }

    val toggleFavouriteResponse by lazy {
        userRepository.toggleFavouriteResponse
    }

    fun getStaff() {
        if (staffId != null) browseRepository.getStaff(staffId!!)
    }

    fun getStaffCharacter() {
        if (characterHasNextPage && staffId != null) browseRepository.getStaffCharacter(staffId!!, characterPage)
    }

    fun getStaffMedia() {
        if (mediaHasNextPage && staffId != null) browseRepository.getStaffMedia(staffId!!, mediaPage)
    }

    fun checkStaffIsFavorite() {
        if (staffId != null) browseRepository.checkStaffIsFavorite(staffId!!)
    }

    fun updateFavorite() {
        if (staffId != null) {
            userRepository.toggleFavourite(null, null, null, staffId, null)
        }
    }
}