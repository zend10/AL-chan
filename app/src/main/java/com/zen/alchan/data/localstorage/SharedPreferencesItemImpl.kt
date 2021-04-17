package com.zen.alchan.data.localstorage

import android.content.Context
import com.google.gson.Gson

class SharedPreferencesManagerImpl(
    private val context: Context,
    private val sharedPreferencesName: String,
    private val gson: Gson
) : SharedPreferencesManager {

    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    private fun getData(key: String) = sharedPreferences.getString(key, null)

    private fun setData(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override var bearerToken: String?
        get() = getData(BEARER_TOKEN)
        set(value) { setData(BEARER_TOKEN, value) }

    companion object {
        private const val BEARER_TOKEN = "bearerToken"
    }
}