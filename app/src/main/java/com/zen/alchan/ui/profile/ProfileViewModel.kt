package com.zen.alchan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.ProfileSection
import com.zen.alchan.helper.utils.Utility

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

    val followersCount by lazy {
        userRepository.followersCount
    }

    val followingsCount by lazy {
        userRepository.followingsCount
    }

    fun initData() {
        userRepository.getViewerData()

        if (currentSection.value == null) {
            _currentSection.postValue(ProfileSection.BIO)
        }

        if (Utility.timeDiffMoreThanOneDay(userRepository.followersCountLastRetrieved)) {
            userRepository.getFollowersCount()
        }

        if (Utility.timeDiffMoreThanOneDay(userRepository.followingsCountLastRetrieved)) {
            userRepository.getFollowingsCount()
        }
    }

    fun setProfileSection(section: ProfileSection) {
        _currentSection.postValue(section)
    }

    fun retrieveViewerData() {
        userRepository.retrieveViewerData()
        userRepository.getFollowersCount()
        userRepository.getFollowingsCount()
    }

    fun triggerRefreshChildFragments() {
        userRepository.triggerRefreshProfilePageChild()
    }
}
