package com.zen.alchan.data.manager

import io.reactivex.Observable

interface UserManager {
    var bearerToken: String?
    val isLoggedIn: Boolean
}