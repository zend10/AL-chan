package com.zen.alchan.ui.studio

import com.zen.alchan.data.response.anilist.Media

interface StudioListener {
    fun navigateToMedia(media: Media)
    fun navigateToStudioMedia()
}