package com.zen.alchan.ui.profile.bio

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.OtherUserRepository
import com.zen.alchan.data.repository.UserRepository
import io.noties.markwon.Markwon

class BioViewModel(private val userRepository: UserRepository,
                   private val otherUserRepository: OtherUserRepository
) : ViewModel() {

    var otherUserId: Int? = null

    val viewerData by lazy {
        userRepository.viewerData
    }

    val userData by lazy {
        otherUserRepository.userData
    }
}