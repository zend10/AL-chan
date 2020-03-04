package com.zen.alchan.ui.profile.bio

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.UserRepository

class BioViewModel(private val userRepository: UserRepository) : ViewModel() {

    val viewerData by lazy {
        userRepository.viewerData
    }
}