package com.zen.alchan.data.manager

import com.zen.alchan.data.response.HomeData
import com.zen.alchan.helper.pojo.SaveItem
import io.reactivex.Observable

interface ContentManager {
    var homeData: SaveItem<HomeData>?
}