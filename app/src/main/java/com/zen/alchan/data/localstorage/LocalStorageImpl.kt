package com.zen.alchan.data.localstorage

import android.content.Context
import com.google.gson.Gson
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.PushNotificationsSettings

class LocalStorageImpl(private val context: Context, private val sharedPreferencesName: String) : LocalStorage {

    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    companion object {
        private const val BEARER_TOKEN = "bearerToken"
        private const val APP_COLOR_THEME = "appColorTheme"
        private const val HOME_SHOW_WATCHING = "homeShowWatching"
        private const val HOME_SHOW_READING = "homeShowReading"
        private const val PUSH_NOTIFICATIONS_SETTINGS = "pushNotificationsSettings"
        private const val VIEWER_DATA = "viewerData"
        private const val VIEWER_DATA_LAST_RETRIEVED = "viewerDataLastRetrieved"
    }

    override var bearerToken: String?
        get() = getData(BEARER_TOKEN)
        set(value) { setData(BEARER_TOKEN, value) }

    override var appColorTheme: AppColorTheme
        get() = AppColorTheme.valueOf(getData(APP_COLOR_THEME) ?: Constant.DEFAULT_THEME.name)
        set(value) { setData(APP_COLOR_THEME, value.name) }

    override var homeShowWatching: Boolean
        get() = if (getData(HOME_SHOW_WATCHING) == null) true else getData(HOME_SHOW_WATCHING)!!.toBoolean()
        set(value) { setData(HOME_SHOW_WATCHING, value.toString()) }

    override var homeShowReading: Boolean
        get() = if (getData(HOME_SHOW_READING) == null) true else getData(HOME_SHOW_READING)!!.toBoolean()
        set(value) { setData(HOME_SHOW_READING, value.toString()) }

    override var pushNotificationsSettings: PushNotificationsSettings
        get() = if (getData(PUSH_NOTIFICATIONS_SETTINGS) == null ) PushNotificationsSettings(true, true, true, true) else gson.fromJson(getData(PUSH_NOTIFICATIONS_SETTINGS), PushNotificationsSettings::class.java)
        set(value) { setData(PUSH_NOTIFICATIONS_SETTINGS, gson.toJson(value)) }

    override var viewerData: User?
        get() = gson.fromJson(getData(VIEWER_DATA), User::class.java)
        set(value) { setData(VIEWER_DATA, gson.toJson(value)) }

    override var viewerDataLastRetrieved: Long?
        get() = getData(VIEWER_DATA_LAST_RETRIEVED)?.toLong()
        set(value) { setData(VIEWER_DATA_LAST_RETRIEVED, value.toString()) }

    private fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    private fun setData(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}