package com.zen.alchan.data.response

import com.google.gson.annotations.SerializedName

data class Announcement(
    val id: String = "",
    val fromDate: String = "",
    val untilDate: String = "",
    val message: String = "",
    val appVersion: Int = 0,
    val requiredUpdate: Boolean = false
)