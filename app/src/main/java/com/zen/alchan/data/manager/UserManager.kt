package com.zen.alchan.data.manager

import com.zen.alchan.data.response.User
import com.zen.alchan.helper.pojo.SaveItem

interface UserManager {
    var bearerToken: String?
    val isLoggedIn: Boolean
    var isLoggedInAsGuest: Boolean
    var viewerData: SaveItem<User>?
}