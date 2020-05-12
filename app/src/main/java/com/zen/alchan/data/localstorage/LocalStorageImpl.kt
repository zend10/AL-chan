package com.zen.alchan.data.localstorage

import android.content.Context
import com.google.gson.Gson
import com.zen.alchan.data.response.MediaTagCollection
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.genericType
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.pojo.PushNotificationsSettings
import type.StaffLanguage

class LocalStorageImpl(private val context: Context,
                       private val sharedPreferencesName: String,
                       private val gson: Gson
) : LocalStorage {

    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    companion object {
        private const val BEARER_TOKEN = "bearerToken"
        private const val APP_COLOR_THEME = "appColorTheme"
        private const val VOICE_ACTOR_LANGUAGE = "voiceActorLanguage"
        private const val PUSH_NOTIFICATIONS_SETTINGS = "pushNotificationsSettings"
        private const val VIEWER_DATA = "viewerData"
        private const val VIEWER_DATA_LAST_RETRIEVED = "viewerDataLastRetrieved"
        private const val GENRE_LIST = "genreList"
        private const val GENRE_LIST_LAST_RETRIEVED = "genreListLastRetrieved"
        private const val TAG_LIST = "tagList"
        private const val TAG_LIST_LAST_RETRIEVED = "tagListLastRetrieved"
        private const val ANIME_LIST_STYLE = "animeListStyle"
        private const val MANGA_LIST_STYLE = "mangaListStyle"
    }

    override var bearerToken: String?
        get() = getData(BEARER_TOKEN)
        set(value) { setData(BEARER_TOKEN, value) }

    override var appColorTheme: AppColorTheme
        get() = AppColorTheme.valueOf(getData(APP_COLOR_THEME) ?: Constant.DEFAULT_THEME.name)
        set(value) { setData(APP_COLOR_THEME, value.name) }

    override var voiceActorLanguage: StaffLanguage
        get() = if (getData(VOICE_ACTOR_LANGUAGE) == null) StaffLanguage.JAPANESE else StaffLanguage.valueOf(getData(VOICE_ACTOR_LANGUAGE)!!)
        set(value) { setData(VOICE_ACTOR_LANGUAGE, value.name) }

    override var pushNotificationsSettings: PushNotificationsSettings
        get() = if (getData(PUSH_NOTIFICATIONS_SETTINGS) == null ) PushNotificationsSettings(true, true, true, true) else gson.fromJson(getData(PUSH_NOTIFICATIONS_SETTINGS), PushNotificationsSettings::class.java)
        set(value) { setData(PUSH_NOTIFICATIONS_SETTINGS, gson.toJson(value)) }

    override var viewerData: User?
        get() = gson.fromJson(getData(VIEWER_DATA), User::class.java)
        set(value) { setData(VIEWER_DATA, gson.toJson(value)) }

    override var viewerDataLastRetrieved: Long?
        get() = getData(VIEWER_DATA_LAST_RETRIEVED)?.toLong()
        set(value) { setData(VIEWER_DATA_LAST_RETRIEVED, value.toString()) }

    override var genreList: List<String?>?
        get() = gson.fromJson(getData(GENRE_LIST), genericType<List<String>>())
        set(value) { setData(GENRE_LIST, gson.toJson(value)) }

    override var genreListLastRetrieved: Long?
        get() = getData(GENRE_LIST_LAST_RETRIEVED)?.toLong()
        set(value) { setData(GENRE_LIST_LAST_RETRIEVED, value.toString()) }

    override var tagList: List<MediaTagCollection>?
        get() = gson.fromJson(getData(TAG_LIST), genericType<List<MediaTagCollection>>())
        set(value) { setData(TAG_LIST, gson.toJson(value)) }

    override var tagListLastRetrieved: Long?
        get() = getData(TAG_LIST_LAST_RETRIEVED)?.toLong()
        set(value) { setData(TAG_LIST_LAST_RETRIEVED, value.toString()) }

    override var animeListStyle: ListStyle?
        get() = gson.fromJson(getData(ANIME_LIST_STYLE), ListStyle::class.java)
        set(value) { setData(ANIME_LIST_STYLE, gson.toJson(value)) }

    override var mangaListStyle: ListStyle?
        get() = gson.fromJson(getData(MANGA_LIST_STYLE), ListStyle::class.java)
        set(value) { setData(MANGA_LIST_STYLE, gson.toJson(value)) }

    private fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    private fun setData(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}