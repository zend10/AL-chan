package com.zen.alchan.data.localstorage

import android.content.Context
import com.google.gson.Gson
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.data.entitiy.ListStyle

class DefaultSharedPreferencesHandler(
    context: Context,
    sharedPreferencesName: String,
    private val gson: Gson
) : SharedPreferencesHandler, BaseSharedPreferencesHandler(context, sharedPreferencesName) {

    override var bearerToken: String?
        get() = getData(BEARER_TOKEN)
        set(value) { setData(BEARER_TOKEN, value) }

    override var guestLogin: Boolean?
        get() = getData(GUEST_LOGIN).toBoolean()
        set(value) { setData(GUEST_LOGIN, value.toString()) }

    override var animeListStyle: ListStyle?
        get() = gson.fromJson(getData(ANIME_LIST_STYLE), ListStyle::class.java)
        set(value) { setData(ANIME_LIST_STYLE, gson.toJson(value)) }

    override var mangaListStyle: ListStyle?
        get() = gson.fromJson(getData(MANGA_LIST_STYLE), ListStyle::class.java)
        set(value) { setData(MANGA_LIST_STYLE, gson.toJson(value)) }

    override var animeFilter: MediaFilter?
        get() = gson.fromJson(getData(ANIME_FILTER), MediaFilter::class.java)
        set(value) { setData(ANIME_FILTER, gson.toJson(value)) }

    override var mangaFilter: MediaFilter?
        get() = gson.fromJson(getData(MANGA_FILTER), MediaFilter::class.java)
        set(value) { setData(MANGA_FILTER, gson.toJson(value)) }

    override var appSetting: AppSetting?
        get() = gson.fromJson(getData(APP_SETTING), AppSetting::class.java)
        set(value) { setData(APP_SETTING, gson.toJson(value)) }

    companion object {
        private const val BEARER_TOKEN = "bearerToken"
        private const val GUEST_LOGIN = "guestLogin"
        private const val ANIME_LIST_STYLE = "animeListStyle"
        private const val MANGA_LIST_STYLE = "mangaListStyle"
        private const val ANIME_FILTER = "animeFilter"
        private const val MANGA_FILTER = "mangaFilter"
        private const val APP_SETTING = "appSetting"
    }
}