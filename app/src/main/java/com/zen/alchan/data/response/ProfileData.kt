package com.zen.alchan.data.response

import com.zen.alchan.data.response.anilist.Page
import com.zen.alchan.data.response.anilist.User

data class ProfileData(
    val following: Page<User> = Page(),
    val followers: Page<User> = Page(),
    val user: User = User()
)