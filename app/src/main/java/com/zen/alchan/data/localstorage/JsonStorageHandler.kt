package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.HomeData
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.pojo.SaveItem
import io.reactivex.Observable

interface JsonStorageHandler {
    var homeData: SaveItem<HomeData>?
    var viewerData: SaveItem<User>?
}