package com.zen.alchan.ui.mangalist.list

import com.zen.alchan.data.response.MediaList

interface MangaListListener {
    fun openEditor(entryId: Int)
    fun openScoreDialog(mediaList: MediaList)
    fun openProgressDialog(mediaList: MediaList, isVolume: Boolean = false)
    fun incrementProgress(mediaList: MediaList, isVolume: Boolean = false)
}