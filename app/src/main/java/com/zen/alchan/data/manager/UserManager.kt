package com.zen.alchan.data.manager

interface UserManager {
    var bearerToken: String?
    val isLoggedIn: Boolean
}