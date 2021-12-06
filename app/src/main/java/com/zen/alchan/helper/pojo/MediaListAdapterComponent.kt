package com.zen.alchan.helper.pojo

import android.net.Uri
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.response.anilist.MediaListOptions

data class MediaListAdapterComponent(
    var listStyle: ListStyle = ListStyle(),
    var appSetting: AppSetting = AppSetting(),
    var mediaListOptions: MediaListOptions = MediaListOptions(),
    var backgroundUri: Uri? = null
)