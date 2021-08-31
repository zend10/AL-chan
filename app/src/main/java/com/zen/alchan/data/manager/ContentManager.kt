package com.zen.alchan.data.manager

import com.zen.alchan.data.response.HomeData
import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.alchan.data.response.Genre
import com.zen.alchan.helper.pojo.SaveItem

interface ContentManager {
    var homeData: SaveItem<HomeData>?
    var genres: SaveItem<List<Genre>>?
    var tags: SaveItem<List<MediaTag>>?
}