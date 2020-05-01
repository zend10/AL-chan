package com.zen.alchan.ui.browse.staff.bio

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository

class StaffBioViewModel(private val browseRepository: BrowseRepository) : ViewModel() {

    var staffId: Int? = null
    var staffData: StaffBioQuery.Staff? = null

    val staffBioData by lazy {
        browseRepository.staffBioData
    }

    fun getStaffBio() {
        if (staffId != null) browseRepository.getStaffBio(staffId!!)
    }
}