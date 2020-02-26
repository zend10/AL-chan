package com.zen.alchan.ui.profile.settings.anilist

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.ProfileRepository
import type.UserTitleLanguage

class AniListSettingsViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    val languageList = listOf(UserTitleLanguage.ROMAJI, UserTitleLanguage.ENGLISH, UserTitleLanguage.NATIVE)
    var selectedTitleLanguage: UserTitleLanguage? = null
    var isInit = false

    val viewerData by lazy {
        profileRepository.viewerData
    }

    val updateAniListSettingsResponse by lazy {
        profileRepository.updateAniListSettingsResponse
    }

    fun initData() {
        profileRepository.getViewerData()

        if (selectedTitleLanguage == null) {
            selectedTitleLanguage = profileRepository.viewerData.value?.options?.titleLanguage
        }
    }

    fun updateAniListSettings( adultContent: Boolean, airingNotifications: Boolean) {
        if (selectedTitleLanguage == null) {
            return
        }
        profileRepository.updateAniListSettings(selectedTitleLanguage!!, adultContent, airingNotifications)
    }
}