package com.zen.alchan.ui.browse.user.list

import type.MediaType

interface UserMediaListener {
    fun openSelectedMedia(mediaId: Int, mediaType: MediaType)
    fun viewMediaListDetail(mediaListId: Int)
}