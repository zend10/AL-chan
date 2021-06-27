package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entitiy.AppSetting

data class UserAvatar(
    val large: String = "",
    val medium: String = ""
) {
    fun getImageUrl(appSetting: AppSetting): String {
        return if (appSetting.useHighestQualityImage)
            large
        else
            medium
    }
}