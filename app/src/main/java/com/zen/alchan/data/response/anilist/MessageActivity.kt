package com.zen.alchan.data.response.anilist

import type.ActivityType

data class MessageActivity(
    val id: Int = 0,
    val type: ActivityType? = null,
    val message: String = ""
)