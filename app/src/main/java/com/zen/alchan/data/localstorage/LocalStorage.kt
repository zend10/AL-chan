package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.MediaTagCollection
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.ListStyle
import type.StaffLanguage

interface LocalStorage {
    var bearerToken: String?

    var appColorTheme: AppColorTheme
    var voiceActorLanguage: StaffLanguage

    var viewerData: User?
    var viewerDataLastRetrieved: Long?

    var followersCount: Int?
    var followersCountLastRetrieved: Long?

    var followingsCount: Int?
    var followingsCountLastRetrieved: Long?

    var genreList: List<String?>?
    var genreListLastRetrieved: Long?

    var tagList: List<MediaTagCollection>?
    var tagListLastRetrieved: Long?

    var animeListStyle: ListStyle?
    var mangaListStyle: ListStyle?

    fun clearStorage()
}