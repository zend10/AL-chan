package com.zen.alchan.data.model

import com.zen.alchan.data.enums.MediaType
import com.zen.alchan.data.model.api.Media
import kotlinx.serialization.Serializable

@Serializable
data class News(
    val id: String = "",
    val media: Media = Media()
) {
    fun getTitle(appConfig: AppConfig): String {
        return media.getTitle(appConfig)
    }

    fun getSubtitle(progressSuffix: String): String {
        if (media.type == null) {
            return ""
        }

        return when (media.type) {
            MediaType.ANIME -> {
                val studio = media.studios
                    .filter { it.isMain }
                    .joinToString(", ") { it.name }
                val episode =
                    media.getCurrentEpisode()?.let { "$progressSuffix $it" } ?: ""
                listOf(studio, episode).filter { it.isNotBlank() }.joinToString(" • ")
            }

            MediaType.MANGA -> {
                val mainRoles = listOf("story", "art")
                val author = media.staff
                    .filter { staff -> mainRoles.any { staff.role.lowercase().contains(it) } }
                    .joinToString(", ") { it.name.full }
                val chapter = media.chapters?.let { "$progressSuffix $it" } ?: ""
                listOf(author, chapter).filter { it.isNotBlank() }.joinToString(" • ")
            }
        }
    }

    fun getImage(appConfig: AppConfig): String {
        return media.bannerImage.ifBlank { media.getCoverImage(appConfig) }
    }
}
