package com.zen.alchan.ui.browse.media.characters

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.helper.pojo.MediaCharacters
import type.MediaType
import type.StaffLanguage

class MediaCharactersViewModel(private val mediaRepository: MediaRepository,
                               private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    var mediaId: Int? = null
    var mediaType: MediaType? = null
    var page = 1
    var hasNextPage = true
    var staffLanguage = appSettingsRepository.voiceActorLanguage

    var isInit = false
    var mediaCharacters = ArrayList<MediaCharacters?>()

    val staffLanguageArray = arrayOf(
        StaffLanguage.JAPANESE.name,
        StaffLanguage.ENGLISH.name,
        StaffLanguage.KOREAN.name,
        StaffLanguage.ITALIAN.name,
        StaffLanguage.SPANISH.name,
        StaffLanguage.PORTUGUESE.name,
        StaffLanguage.FRENCH.name,
        StaffLanguage.GERMAN.name,
        StaffLanguage.HEBREW.name,
        StaffLanguage.HUNGARIAN.name
    )

    val mediaCharactersData by lazy {
        mediaRepository.mediaCharactersData
    }

    fun getMediaCharacters() {
        if (hasNextPage && mediaId != null) mediaRepository.getMediaCharacters(mediaId!!, page)
    }
}