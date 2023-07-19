package com.zen.alchan.data.manager

import com.zen.alchan.data.localstorage.JsonStorageHandler
import com.zen.alchan.data.response.HomeData
import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.alchan.data.response.Genre
import com.zen.alchan.helper.pojo.SaveItem

class DefaultContentManager(private val jsonStorageHandler: JsonStorageHandler) : ContentManager {

    override var homeData: SaveItem<HomeData>?
        get() = jsonStorageHandler.homeData
        set(value) { jsonStorageHandler.homeData = value }

    override var genres: SaveItem<List<Genre>>?
        get() = jsonStorageHandler.genres
        set(value) { jsonStorageHandler.genres = value }

    override var tags: SaveItem<List<MediaTag>>?
        get() = jsonStorageHandler.tags
        set(value) { jsonStorageHandler.tags = value }
}