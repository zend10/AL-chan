package com.zen.alchan.data.localstorage

import android.content.Context
import com.google.gson.Gson
import com.zen.alchan.data.response.MediaTagCollection
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.genericType
import com.zen.alchan.helper.pojo.AppSettings
import com.zen.alchan.helper.pojo.ListStyle
import type.StaffLanguage

class LocalStorageImpl(private val context: Context,
                       private val sharedPreferencesName: String,
                       private val gson: Gson
) : LocalStorage {

    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    companion object {
        private const val BEARER_TOKEN = "bearerToken"
        private const val APP_SETTINGS = "appSettings"
        private const val VIEWER_DATA = "viewerData"
        private const val VIEWER_DATA_LAST_RETRIEVED = "viewerDataLastRetrieved"
        private const val FOLLOWERS_COUNT = "followersCount"
        private const val FOLLOWERS_COUNT_LAST_RETRIEVED = "followersCountLastRetrieved"
        private const val FOLLOWINGS_COUNT = "followingsCount"
        private const val FOLLOWINGS_COUNT_LAST_RETRIEVED = "followingsCountLastRetrieved"
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

    override var appSettings: AppSettings
        get() = if (getData(APP_SETTINGS) == null) AppSettings() else gson.fromJson(getData(APP_SETTINGS), AppSettings::class.java)
        set(value) { setData(APP_SETTINGS, gson.toJson(value)) }

    override var viewerData: User?
        get() = gson.fromJson(getData(VIEWER_DATA), User::class.java)
        set(value) { setData(VIEWER_DATA, gson.toJson(value)) }

    override var viewerDataLastRetrieved: Long?
        get() = getData(VIEWER_DATA_LAST_RETRIEVED)?.toLong()
        set(value) { setData(VIEWER_DATA_LAST_RETRIEVED, value.toString()) }

    override var followersCount: Int?
        get() = getData(FOLLOWERS_COUNT)?.toInt()
        set(value) { setData(FOLLOWERS_COUNT, value.toString()) }

    override var followersCountLastRetrieved: Long?
        get() = getData(FOLLOWERS_COUNT_LAST_RETRIEVED)?.toLong()
        set(value) { setData(FOLLOWERS_COUNT_LAST_RETRIEVED, value.toString()) }

    override var followingsCount: Int?
        get() = getData(FOLLOWINGS_COUNT)?.toInt()
        set(value) { setData(FOLLOWINGS_COUNT, value.toString()) }

    override var followingsCountLastRetrieved: Long?
        get() = getData(FOLLOWINGS_COUNT_LAST_RETRIEVED)?.toLong()
        set(value) { setData(FOLLOWINGS_COUNT_LAST_RETRIEVED, value.toString()) }

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

    override fun clearStorage() {
        sharedPreferences.edit().clear().apply()
    }
}