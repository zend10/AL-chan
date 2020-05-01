package com.zen.alchan.ui.browse.staff.anime

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.helper.pojo.StaffMedia

class StaffAnimeViewModel(private val browseRepository: BrowseRepository) : ViewModel() {

    var staffId: Int? = null
    var page = 1
    var hasNextPage = true

    var isInit = false
    var staffMedia = ArrayList<StaffMedia?>()

    val staffMediaData by lazy {
        browseRepository.staffAnimeData
    }

    fun getStaffMedia() {
        if (hasNextPage && staffId != null) browseRepository.getStaffAnime(staffId!!, page)
    }
}