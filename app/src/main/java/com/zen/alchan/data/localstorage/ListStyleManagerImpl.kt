package com.zen.alchan.data.localstorage

import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.pojo.ListStyle

class ListStyleManagerImpl(private val localStorage: LocalStorage) : ListStyleManager {

    /*
    override val appSettings: AppSettings
        get() {
            val savedSettings = localStorage.appSettings
            if (savedSettings.appTheme == null) savedSettings.appTheme = AppColorTheme.YELLOW
            if (savedSettings.circularAvatar == null) savedSettings.circularAvatar = true
            if (savedSettings.whiteBackgroundAvatar == null) savedSettings.whiteBackgroundAvatar = true
            if (savedSettings.voiceActorLanguage == null) savedSettings.voiceActorLanguage = StaffLanguage.JAPANESE
            // add more to here when adding new settings
            return savedSettings
        }
     */

    override val animeListStyle: ListStyle
        get() {
            val savedStyle = localStorage.animeListStyle
            if (savedStyle.listType == null) savedStyle.listType = ListType.LINEAR
            if (savedStyle.backgroundImage == null) savedStyle.backgroundImage = false
            if (savedStyle.longPressViewDetail == null) savedStyle.longPressViewDetail = true
            if (savedStyle.hideMangaVolume == null) savedStyle.hideMangaVolume = false
            if (savedStyle.hideMangaChapter == null) savedStyle.hideMangaChapter = false
            if (savedStyle.hideNovelVolume == null) savedStyle.hideNovelVolume = false
            if (savedStyle.hideNovelChapter == null) savedStyle.hideNovelChapter = false
            if (savedStyle.showNotesIndicator == null) savedStyle.showNotesIndicator = false
            if (savedStyle.showPriorityIndicator == null) savedStyle.showPriorityIndicator = false
            if (savedStyle.hideMediaFormat == null) savedStyle.hideMediaFormat = false
            if (savedStyle.hideScoreWhenNotScored == null) savedStyle.hideScoreWhenNotScored = false
            if (savedStyle.hideAiringIndicator == null) savedStyle.hideAiringIndicator = false
            // add more to here when adding new settings
            return savedStyle
        }

    override val mangaListStyle: ListStyle
        get() {
            val savedStyle = localStorage.mangaListStyle
            if (savedStyle.listType == null) savedStyle.listType = ListType.LINEAR
            if (savedStyle.backgroundImage == null) savedStyle.backgroundImage = false
            if (savedStyle.longPressViewDetail == null) savedStyle.longPressViewDetail = true
            if (savedStyle.hideMangaVolume == null) savedStyle.hideMangaVolume = false
            if (savedStyle.hideMangaChapter == null) savedStyle.hideMangaChapter = false
            if (savedStyle.hideNovelVolume == null) savedStyle.hideNovelVolume = false
            if (savedStyle.hideNovelChapter == null) savedStyle.hideNovelChapter = false
            if (savedStyle.showNotesIndicator == null) savedStyle.showNotesIndicator = false
            if (savedStyle.showPriorityIndicator == null) savedStyle.showPriorityIndicator = false
            if (savedStyle.hideMediaFormat == null) savedStyle.hideMediaFormat = false
            if (savedStyle.hideScoreWhenNotScored == null) savedStyle.hideScoreWhenNotScored = false
            if (savedStyle.hideAiringIndicator == null) savedStyle.hideAiringIndicator = false
            // add more to here when adding new settings
            return savedStyle
        }

    override fun saveAnimeListStyle(newAnimeListStyle: ListStyle) {
        localStorage.animeListStyle = newAnimeListStyle
    }

    override fun saveMangaListStyle(newMangaListStyle: ListStyle) {
        localStorage.mangaListStyle = newMangaListStyle
    }
}