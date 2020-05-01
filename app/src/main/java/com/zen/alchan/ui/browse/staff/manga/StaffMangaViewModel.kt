package com.zen.alchan.ui.browse.staff.manga

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.helper.pojo.StaffMedia

class StaffMangaViewModel(private val browseRepository: BrowseRepository) : ViewModel() {

    var staffId: Int? = null
    var page = 1
    var hasNextPage = true

    var isInit = false
    var staffMedia = ArrayList<StaffMedia?>()

    val staffMediaData by lazy {
        browseRepository.staffMangaData
    }

    fun getStaffMedia() {
        if (hasNextPage && staffId != null) browseRepository.getStaffManga(staffId!!, page)
    }
}