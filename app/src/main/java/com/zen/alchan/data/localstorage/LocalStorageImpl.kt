package com.zen.alchan.data.localstorage

import android.content.Context
import com.google.gson.Gson
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.AppColorTheme

class LocalStorageImpl(private val context: Context, private val sharedPreferencesName: String) : LocalStorage {

    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    companion object {
        private const val BEARER_TOKEN = "bearerToken"
        private const val APP_COLOR_THEME = "appColorTheme"
        private const val HOME_SHOW_WATCHING = "homeShowWatching"
        private const val HOME_SHOW_READING = "homeShowReading"
        private const val PUSH_NOTIF_AIRING = "pushNotifAiring"
        private const val PUSH_NOTIF_ACTIVITY = "pushNotifActivity"
        private const val PUSH_NOTIF_FORUM = "pushNotifForum"
        private const val PUSH_NOTIF_FOLLOWS = "pushNotifFollows"
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

    override var pushNotifAiring: Boolean
        get() = if (getData(PUSH_NOTIF_AIRING) == null) true else getData(PUSH_NOTIF_AIRING)!!.toBoolean()
        set(value) { setData(PUSH_NOTIF_AIRING, value.toString()) }

    override var pushNotifActivity: Boolean
        get() = if (getData(PUSH_NOTIF_ACTIVITY) == null) true else getData(PUSH_NOTIF_ACTIVITY)!!.toBoolean()
        set(value) { setData(PUSH_NOTIF_ACTIVITY, value.toString()) }

    override var pushNotifForum: Boolean
        get() = if (getData(PUSH_NOTIF_FORUM) == null) true else getData(PUSH_NOTIF_FORUM)!!.toBoolean()
        set(value) { setData(PUSH_NOTIF_FORUM, value.toString()) }

    override var pushNotifFollows: Boolean
        get() = if (getData(PUSH_NOTIF_FOLLOWS) == null) true else getData(PUSH_NOTIF_FOLLOWS)!!.toBoolean()
        set(value) { setData(PUSH_NOTIF_FOLLOWS, value.toString()) }

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