package com.zen.alchan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.ProfileSection

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _currentSection = MutableLiveData<ProfileSection>()
    val currentSection: LiveData<ProfileSection>
        get() = _currentSection

    val viewerDataResponse by lazy {
        userRepository.viewerDataResponse
    }

    val viewerData by lazy {
        userRepository.viewerData
    }

    fun initData() {
        userRepository.getViewerData()

        if (currentSection.value == null) {
            _currentSection.postValue(ProfileSection.BIO)
        }

        // TODO: get following and followers count
    }

    fun setProfileSection(section: ProfileSection) {
        _currentSection.postValue(section)
    }

    fun retrieveViewerData() {
        userRepository.retrieveViewerData()
    }

    fun triggerRefreshChildFragments() {
        userRepository.triggerRefreshProfilePageChild()
    }
}
