package com.zen.alchan.ui.medialist

import android.content.Context
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

abstract class BaseMediaListRvAdapter(
    private val context: Context,
    list: List<MediaListItem>
) : BaseRecyclerViewAdapter<MediaListItem>(list) {

    protected var listStyle = ListStyle.EMPTY_LIST_STYLE
    protected var mediaListOptions = MediaListOptions()

    fun applyListStyle(listStyle: ListStyle) {
        this.listStyle = listStyle
    }

    fun applyMediaListOptions(mediaListOptions: MediaListOptions) {
        this.mediaListOptions = mediaListOptions
    }
}