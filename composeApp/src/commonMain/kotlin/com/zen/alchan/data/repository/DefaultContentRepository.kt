package com.zen.alchan.data.repository

import com.zen.alchan.data.model.api.AiringSchedule
import com.zen.alchan.data.model.api.Media
import com.zen.alchan.data.model.api.MediaCoverImage
import com.zen.alchan.data.model.api.MediaTitle
import com.zen.alchan.data.provider.ApiProvider

class DefaultContentRepository(private val apiProvider: ApiProvider) : ContentRepository {
    override suspend fun getTrending(): List<Media> {
        return apiProvider.getTrending().trendingAnime.data.map {
            Media(
                id = it.id ?: 0,
                idMal = it.idMal ?: 0,
                title = MediaTitle(
                    romaji = it.title?.romaji ?: "",
                    english = it.title?.english ?: "",
                    native = it.title?.native ?: "",
                    userPreferred = it.title?.userPreferred ?: ""
                ),
                bannerImage = it.bannerImage ?: "",
                coverImage = MediaCoverImage(
                    color = it.coverImage?.color ?: "",
                    extraLarge = it.coverImage?.extraLarge ?: "",
                    large = it.coverImage?.large ?: "",
                    medium = it.coverImage?.medium ?: ""
                ),
                episodes = it.episodes,
                chapters = it.chapters,
                volumes = it.volumes,
                nextAiringEpisode = AiringSchedule(
                    episode = it.nextAiringEpisode?.episode ?: 0
                )
            )
        }
    }
}