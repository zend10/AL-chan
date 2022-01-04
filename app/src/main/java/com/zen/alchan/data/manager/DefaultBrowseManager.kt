package com.zen.alchan.data.manager

import com.zen.alchan.data.localstorage.SharedPreferencesHandler
import com.zen.alchan.helper.enums.ListType

class DefaultBrowseManager(private val sharedPreferencesHandler: SharedPreferencesHandler) : BrowseManager {

    override var othersListType: ListType
        get() = sharedPreferencesHandler.othersListType ?: ListType.LINEAR
        set(value) { sharedPreferencesHandler.othersListType = value }
}