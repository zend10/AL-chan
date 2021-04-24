package com.zen.alchan.data.localstorage

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zen.alchan.data.response.HomeData
import com.zen.alchan.helper.pojo.SaveItem
import io.reactivex.Observable

class DefaultJsonStorageHandler(
    context: Context,
    private val gson: Gson
) : JsonStorageHandler, BaseJsonStorageHandler(context) {

    override var homeData: SaveItem<HomeData>?
        get() = gson.fromJson(getData(HOME_DATA), getType<SaveItem<HomeData>>())
        set(value) { setData(HOME_DATA, gson.toJson(value)) }

    companion object {
        private const val HOME_DATA = "homeData.json"
    }
}