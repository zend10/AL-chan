package com.zen.alchan.data.model.api

import com.zen.alchan.data.enums.Country
import com.zen.alchan.data.enums.MediaFormat
import com.zen.alchan.data.enums.MediaStatus
import com.zen.alchan.data.enums.MediaType
import com.zen.alchan.data.model.AppConfig
import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val id: String = "",
    val idMal: String = "",
    val title: MediaTitle = MediaTitle(),
    val countryOfOrigin: Country? = null,
    val type: MediaType? = null,
    val status: MediaStatus? = null,
    val format: MediaFormat? = null,
    val description: String = "",
    val coverImage: MediaCoverImage = MediaCoverImage(),
    val bannerImage: String = "",
    val chapters: Int? = null,
    val volumes: Int? = null,
    val episodes: Int? = null,
    val genres: List<String> = listOf(),
    val averageScore: Int = 0,
    val favourites: Int = 0,
    val staff: List<Staff> = listOf(),
    val studios: List<Studio> = listOf(),
    val startDate: FuzzyDate? = null,
    val nextAiringEpisode: AiringSchedule? = null
) {
    fun getTitle(appConfig: AppConfig): String {
        return title.userPreferred
    }

    fun getCoverImage(appConfig: AppConfig): String {
        return coverImage.extraLarge
    }

    fun getCurrentEpisode(): Int? {
        if (nextAiringEpisode != null && nextAiringEpisode.episode > 1) {
            return nextAiringEpisode.episode - 1
        }

        if (episodes != null && episodes > 0) {
            return episodes
        }

        return null
    }
}