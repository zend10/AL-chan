package com.zen.alchan.ui.media

import com.zen.alchan.data.response.AnimeTheme
import com.zen.alchan.data.response.AnimeThemeEntry
import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.type.MediaSeason
import com.zen.alchan.type.MediaType

interface MediaListener {

    interface MediaSynopsisListener {
        fun toggleShowMore(shouldShowMore: Boolean)
    }

    interface MediaInfoListener {
        fun copyTitle(title: String)
        fun navigateToExplore(type: MediaType, season: MediaSeason, seasonYear: Int)
    }

    interface MediaGenreListener {
        fun navigateToExplore(type: MediaType, genre: Genre)
    }

    interface MediaCharacterListener {
        fun navigateToMediaCharacters(media: Media)
        fun navigateToCharacter(character: Character)
    }

    interface MediaStudioListener {
        fun navigateToStudio(studio: Studio)
    }

    interface MediaTagsListener {
        fun shouldShowSpoilers(show: Boolean)
        fun navigateToExplore(type: MediaType, tag: MediaTag)
        fun showDescription(tag: MediaTag)
    }

    interface MediaThemesListener {
        fun openThemeDialog(media: Media, animeTheme: AnimeTheme, animeThemeEntry: AnimeThemeEntry?)
        fun openGroupDialog(viewType: Int, groups: List<String>)
    }

    interface MediaStaffListener {
        fun navigateToMediaStaff(media: Media)
        fun navigateToStaff(staff: Staff)
    }

    interface MediaRelationsListener {
        fun navigateToMedia(media: Media)
    }

    interface MediaRecommendationsListener {
        fun navigateToMedia(media: Media)
    }

    interface MediaLinksListener {
        fun navigateToUrl(mediaExternalLink: MediaExternalLink)
        fun copyExternalLink(mediaExternalLink: MediaExternalLink)
    }

    val mediaSynopsisListener: MediaSynopsisListener
    val mediaInfoListener: MediaInfoListener
    val mediaGenreListener: MediaGenreListener
    val mediaCharacterListener: MediaCharacterListener
    val mediaStudioListener: MediaStudioListener
    val mediaTagsListener: MediaTagsListener
    val mediaThemesListener: MediaThemesListener
    val mediaStaffListener: MediaStaffListener
    val mediaRelationsListener: MediaRelationsListener
    val mediaRecommendationsListener: MediaRecommendationsListener
    val mediaLinksListener: MediaLinksListener
}