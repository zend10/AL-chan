package com.zen.alchan.data.response.github

import com.google.gson.annotations.SerializedName

data class AnnouncementResponse(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("from_date")
    val fromDate: String? = null,
    @SerializedName("until_date")
    val untilDate: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("app_version")
    val appVersion: String? = null,
    @SerializedName("required_update")
    val requiredUpdate: String? = null
)