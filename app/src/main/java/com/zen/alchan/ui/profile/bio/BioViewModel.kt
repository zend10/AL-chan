package com.zen.alchan.ui.profile.bio

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.ProfileRepository

class BioViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    val viewerData by lazy {
        profileRepository.viewerData
    }
}