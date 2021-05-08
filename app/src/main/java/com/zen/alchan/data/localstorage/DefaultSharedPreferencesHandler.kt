package com.zen.alchan.data.localstorage

import android.content.Context
import com.google.gson.Gson
import com.zen.alchan.data.entitiy.AppSetting

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

    override var appSetting: AppSetting?
        get() = gson.fromJson(getData(APP_SETTING), AppSetting::class.java)
        set(value) { setData(APP_SETTING, gson.toJson(value)) }

    companion object {
        private const val BEARER_TOKEN = "bearerToken"
        private const val GUEST_LOGIN = "guestLogin"
        private const val APP_SETTING = "appSetting"
    }
}