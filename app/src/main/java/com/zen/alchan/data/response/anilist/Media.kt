package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
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
    val isLicensed: Boolean? = null,
    val source: MediaSource? = null,
    val trailer: MediaTrailer? = null,
    val coverImage: MediaCoverImage = MediaCoverImage(),
    val bannerImage: String = "",
    val genres: List<Genre> = listOf(),
    val synonyms: List<String> = listOf(),
    val averageScore: Int = 0,
    val meanScore: Int = 0,
    val popularity: Int = 0,
    val trending: Int = 0,
    val favourites: Int = 0,
    val tags: List<MediaTag> = listOf(),
    val relations: MediaConnection = MediaConnection(),
    val characters: CharacterConnection = CharacterConnection(),
    val staff: StaffConnection = StaffConnection(),
    val studios: StudioConnection = StudioConnection(),
    var isFavourite: Boolean = false,
    val isAdult: Boolean = false,
    val nextAiringEpisode: AiringSchedule? = null,
    val airingSchedule: AiringScheduleConnection = AiringScheduleConnection(),
    val externalLinks: List<MediaExternalLink> = listOf(),
    val rankings: List<MediaRank> = listOf(),
    val recommendations: RecommendationConnection = RecommendationConnection(),
    val stats: MediaStats? = null,
    val siteUrl: String = "",
    val mediaListEntry: MediaList? = null
) {
    fun getId() : Int {
        return idAniList
    }

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
            MediaNaming.ROMAJI -> if (title.romaji.isNotBlank()) title.romaji else title.userPreferred
            MediaNaming.ENGLISH -> if (title.english.isNotBlank()) title.english else title.userPreferred
            MediaNaming.NATIVE -> if (title.native.isNotBlank()) title.native else title.userPreferred
        }
    }

    fun getCoverImage(appSetting: AppSetting): String {
        return if (appSetting.useHighestQualityImage)
            coverImage.extraLarge
        else
            coverImage.large
    }

    fun getLength(): Int? {
        return when (type) {
            MediaType.ANIME -> episodes
            MediaType.MANGA -> chapters
            else -> null
        }
    }

    fun getMainStaff(): List<StaffEdge> {
        val mainStaff = when (type) {
            MediaType.ANIME -> staff.edges.filter {
                val role = it.role.trim()
                role.equals("original creator", true) ||
                role.equals("director", true) ||
                role.equals("original character design", true) ||
                role.equals("original story", true)
            }
            MediaType.MANGA -> staff.edges.filter {
                val role = it.role.trim()
                role.equals("story & art", true) ||
                role.equals("story", true) ||
                role.equals("art", true) ||
                role.equals("original creator", true) ||
                role.equals("original character design", true) ||
                role.equals("original story", true) ||
                role.equals("illustration", true) ||
                role.equals("character design", true)
            }
            else -> listOf()
        }.distinctBy { it.node.id }

        if (mainStaff.isEmpty()) {
            return staff.edges.take(10)
        }

        return mainStaff.take(10)
    }
}