package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.helper.enums.Country
import com.zen.alchan.helper.enums.MediaNaming
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.data.response.Genre
import type.*

data class Media(
    val idAniList: Int = 0,
    val idMal: Int? = null,
    val title: MediaTitle = MediaTitle(),
    val type: MediaType? = null,
    val format: MediaFormat? = null,
    val status: MediaStatus? = null,
    val description: String = "",
    val startDate: FuzzyDate? = null,
    val endDate: FuzzyDate? = null,
    val season: MediaSeason? = null,
    val seasonYear: Int? = null,
    val episodes: Int? = null,
    val duration: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val countryOfOrigin: String? = null,
    val source: MediaSource? = null,
//    val trailer: MediaTrailer? = null,
    val coverImage: MediaCoverImage = MediaCoverImage(),
    val bannerImage: String = "",
    val genres: List<Genre> = listOf(),
    val synonyms: List<String> = listOf(),
    val averageScore: Int = 0,
    val meanScore: Int = 0,
    val popularity: Int = 0,
    val trending: Int = 0,
    val staffs: StaffConnection = StaffConnection(),
    val studios: StudioConnection = StudioConnection(),
    val favourites: Int = 0,
    val tags: List<MediaTag> = listOf(),
    val isFavourite: Boolean = false,
    val isAdult: Boolean = false,
    val nextAiringEpisode: AiringSchedule? = null,
    val externalLinks: List<MediaExternalLink> = listOf()
) {
    fun getFormattedMediaFormat(toUpper: Boolean = false): String {
        return format?.name?.convertFromSnakeCase(toUpper) ?: ""
    }

    fun getTitle(appSetting: AppSetting): String {
        return when (countryOfOrigin) {
            Country.JAPAN.iso -> getPreferredNaming(appSetting.japaneseMediaNaming)
            Country.SOUTH_KOREA.iso -> getPreferredNaming(appSetting.koreanMediaNaming)
            Country.CHINA.iso -> getPreferredNaming(appSetting.chineseMediaNaming)
            Country.TAIWAN.iso -> getPreferredNaming(appSetting.taiwaneseMediaNaming)
            else -> getPreferredNaming(MediaNaming.FOLLOW_ANILIST)
        }
    }

    private fun getPreferredNaming(naming: MediaNaming): String {
        return when (naming) {
            MediaNaming.FOLLOW_ANILIST -> title.userPreferred
            MediaNaming.ROMAJI -> title.romaji
            MediaNaming.ENGLISH -> title.english
            MediaNaming.NATIVE -> title.native
        }
    }
}