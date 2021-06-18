package com.zen.alchan.helper.pojo

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.response.anilist.MediaListOptions

data class MediaListAdapterComponent(
    var listStyle: ListStyle = ListStyle.EMPTY_LIST_STYLE,
    var appSetting: AppSetting = AppSetting.EMPTY_APP_SETTING,
    var mediaListOptions: MediaListOptions = MediaListOptions(),
    var mediaListItems: List<MediaListItem> = listOf()
) {
    companion object {
        val EMPTY_MEDIA_LIST_ADAPTER_COMPONENT = MediaListAdapterComponent()
    }
}