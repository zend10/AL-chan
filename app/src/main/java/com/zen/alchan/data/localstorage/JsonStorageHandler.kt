package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.HomeData
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.pojo.SaveItem

interface JsonStorageHandler {
    var homeData: SaveItem<HomeData>?
    var viewerData: SaveItem<User>?
    var profileData: SaveItem<ProfileData>?
}