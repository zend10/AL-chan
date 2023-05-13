package com.zen.alchan.data.manager

import com.zen.alchan.BuildConfig
import com.zen.alchan.data.localstorage.SharedPreferencesHandler
import com.zen.alchan.helper.enums.ListType

class DefaultBrowseManager(private val sharedPreferencesHandler: SharedPreferencesHandler) : BrowseManager {

    override var othersListType: ListType
        get() = sharedPreferencesHandler.othersListType ?: ListType.LINEAR
        set(value) { sharedPreferencesHandler.othersListType = value }

    override val youTubeApiKey: String
        get() = BuildConfig.YOUTUBE_API_KEY
}