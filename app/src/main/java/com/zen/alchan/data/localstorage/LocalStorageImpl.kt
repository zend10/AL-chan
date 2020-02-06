package com.zen.alchan.data.localstorage

import android.content.Context
import com.google.gson.Gson
import com.zen.alchan.helper.enums.AppColorTheme

class LocalStorageImpl(private val context: Context, private val sharedPreferencesName: String) : LocalStorage {

    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    companion object {
        private const val APP_COLOR_THEME = "appColorTheme"
    }

    override var appColorTheme: AppColorTheme
        get() = AppColorTheme.valueOf(getData(APP_COLOR_THEME) ?: AppColorTheme.YELLOW.name)
        set(value) { setData(APP_COLOR_THEME, value.name) }

    private fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    private fun setData(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}