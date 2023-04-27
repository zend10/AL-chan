package com.zen.alchan.helper.pojo

import com.zen.alchan.data.entity.AppSetting
import type.MediaSort

data class StudioMediaListAdapterComponent(
    val appSetting: AppSetting = AppSetting(),
    val mediaSort: MediaSort = MediaSort.POPULARITY_DESC
)