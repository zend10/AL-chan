package com.zen.alchan.data.localstorage

import android.content.Context

abstract class BaseSharedPreferencesHandler(
    private val context: Context,
    private val sharedPreferencesName: String
) {

    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    protected fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    protected fun setData(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    protected fun clearData() {
        sharedPreferences.edit().clear().apply()
    }
}