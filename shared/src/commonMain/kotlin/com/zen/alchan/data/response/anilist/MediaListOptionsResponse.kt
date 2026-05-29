package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.enums.ScoreFormat
import com.zen.alchan.data.enums.getScoreFormatEnum
import com.zen.alchan.data.model.api.MediaListOptions
import com.zen.alchan.data.model.api.MediaListTypeOptions
import kotlinx.serialization.Serializable

@Serializable
data class MediaListOptionsResponse(
    val scoreFormat: String? = null,
    val rowOrder: String? = null,
    val animeList: MediaListTypeOptionsResponse? = null,
    val mangaList: MediaListTypeOptionsResponse? = null
) {
    fun toModel(): MediaListOptions {
        return MediaListOptions(
            scoreFormat = getScoreFormatEnum(scoreFormat) ?: ScoreFormat.POINT_100,
            rowOrder = rowOrder ?: "",
            animeList = animeList?.toModel() ?: MediaListTypeOptions(),
            mangaList = mangaList?.toModel() ?: MediaListTypeOptions()
        )
    }
}
