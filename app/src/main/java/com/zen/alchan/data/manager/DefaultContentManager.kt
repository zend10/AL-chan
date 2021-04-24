package com.zen.alchan.data.manager

import com.zen.alchan.data.localstorage.JsonStorageHandler
import com.zen.alchan.data.response.HomeData
import com.zen.alchan.helper.pojo.SaveItem
import io.reactivex.Observable

class DefaultContentManager(private val jsonStorageHandler: JsonStorageHandler) : ContentManager {

    override var homeData: SaveItem<HomeData>?
        get() = jsonStorageHandler.homeData
        set(value) { jsonStorageHandler.homeData = value }
}