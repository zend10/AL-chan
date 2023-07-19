package com.zen.alchan.helper.pojo

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.User

data class SocialAdapterComponent(
    val viewer: User? = null,
    val appSetting: AppSetting = AppSetting()
)