package com.zen.alchan.helper.pojo

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.ListType

data class SeasonalAdapterComponent(
    val listType: ListType = ListType.LINEAR,
    val appSetting: AppSetting = AppSetting()
)