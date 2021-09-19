package com.zen.alchan.helper.pojo

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.data.response.anilist.MediaListOptions

data class MediaListAdapterComponent(
    var listStyle: ListStyle = ListStyle(),
    var appSetting: AppSetting = AppSetting(),
    var mediaListOptions: MediaListOptions = MediaListOptions()
)