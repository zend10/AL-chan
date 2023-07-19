package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.HomeData
import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.helper.pojo.SaveItem

interface JsonStorageHandler {
    var homeData: SaveItem<HomeData>?
    var viewerData: SaveItem<User>?
    var genres: SaveItem<List<Genre>>?
    var tags: SaveItem<List<MediaTag>>?
    var animeList: SaveItem<MediaListCollection>?
    var mangaList: SaveItem<MediaListCollection>?
}