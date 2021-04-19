package com.zen.alchan.data.localstorage

import android.content.Context
import com.google.gson.Gson

class DefaultSharedPreferencesHandler(
    context: Context,
    sharedPreferencesName: String,
    private val gson: Gson
) : SharedPreferencesHandler, BaseSharedPreferencesHandler(context, sharedPreferencesName) {

    override var bearerToken: String?
        get() = getData(BEARER_TOKEN)
        set(value) { setData(BEARER_TOKEN, value) }

    companion object {
        private const val BEARER_TOKEN = "bearerToken"
    }
}