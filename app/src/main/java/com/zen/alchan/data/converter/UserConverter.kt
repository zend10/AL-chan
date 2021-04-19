package com.zen.alchan.data.converter

import com.zen.alchan.data.response.User

fun ViewerQuery.Data.convert(): User {
    return User(
        id = viewer?.id ?: 0,
        name = viewer?.name ?: ""
    )
}