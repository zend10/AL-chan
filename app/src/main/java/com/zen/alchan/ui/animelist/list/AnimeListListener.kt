package com.zen.alchan.ui.animelist.list

import com.zen.alchan.data.response.Media
import com.zen.alchan.data.response.MediaList

interface AnimeListListener {
    fun openEditor(entryId: Int)
    fun openScoreDialog(mediaList: MediaList)
    fun openProgressDialog(mediaList: MediaList)
    fun incrementProgress(mediaList: MediaList)
    fun openBrowsePage(media: Media)
    fun showDetail(entryId: Int)
}