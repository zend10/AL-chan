package com.zen.alchan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.ProfileRepository
import com.zen.alchan.helper.enums.ProfileSection

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    private val _currentSection = MutableLiveData<ProfileSection>()
    val currentSection: LiveData<ProfileSection>
        get() = _currentSection

    val viewerDataResponse by lazy {
        profileRepository.viewerDataResponse
    }

    val viewerData by lazy {
        profileRepository.viewerData
    }

    fun initData() {
        profileRepository.getViewerData()

        if (currentSection.value == null) {
            _currentSection.postValue(ProfileSection.BIO)
        }

        // TODO: get following and followers count
    }

    fun setProfileSection(section: ProfileSection) {
        _currentSection.postValue(section)
    }

    fun retrieveViewerData() {
        profileRepository.retrieveViewerData()
    }
}
