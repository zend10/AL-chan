package com.zen.alchan.ui.browse.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.OtherUserRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.ProfileSection

class UserViewModel(private val otherUserRepository: OtherUserRepository,
                    private val userRepository: UserRepository,
                    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _currentSection = MutableLiveData<ProfileSection>()
    val currentSection: LiveData<ProfileSection>
        get() = _currentSection

    var userId: Int? = null
    var currentIsFollowing: Boolean? = null

    val currentUserId: Int
        get() = userRepository.viewerData.value?.id!!

    val userDataResponse by lazy {
        otherUserRepository.userDataResponse
    }

    val userData by lazy {
        otherUserRepository.userData
    }

    val followersCount by lazy {
        otherUserRepository.followersCount
    }

    val followingsCount by lazy {
        otherUserRepository.followingsCount
    }

    val toggleFollowingResponse by lazy {
        userRepository.toggleFollowResponse
    }

    val circularAvatar
        get() = appSettingsRepository.appSettings.circularAvatar == true

    val whiteBackgroundAvatar
        get() = appSettingsRepository.appSettings.whiteBackgroundAvatar == true

    fun initData() {
        if (userId == null) {
            return
        }

        otherUserRepository.retrieveUserData(userId!!)
        otherUserRepository.getFollowersCount(userId!!)
        otherUserRepository.getFollowingsCount(userId!!)

        if (currentSection.value == null) {
            _currentSection.postValue(ProfileSection.BIO)
        }
    }

    fun setProfileSection(section: ProfileSection) {
        _currentSection.postValue(section)
    }

    fun retrieveUserData() {
        if (userId == null) {
            return
        }

        otherUserRepository.retrieveUserData(userId!!)
        otherUserRepository.getFollowersCount(userId!!)
        otherUserRepository.getFollowingsCount(userId!!)
    }

    fun triggerRefreshChildFragments() {
        if (userId == null) {
            return
        }

        otherUserRepository.triggerRefreshProfilePageChild(userId!!)
    }

    fun toggleFollow() {
        if (userId == null) {
            return
        }

        userRepository.toggleFollow(userId!!)
    }

    fun refreshFollowingCount() {
        if (userId == null) {
            return
        }

        otherUserRepository.getFollowingsCount(userId!!)
        otherUserRepository.getFollowersCount(userId!!)
    }
}