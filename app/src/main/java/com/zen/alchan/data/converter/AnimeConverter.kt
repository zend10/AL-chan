package com.zen.alchan.data.converter

import com.zen.alchan.data.response.*
import com.zen.alchan.data.response.animethemes.AnimePaginationResponse
import com.zen.alchan.data.response.mal.AnimeResponse

fun AnimePaginationResponse.convert(): Anime {
    val anime = anime?.firstOrNull() ?: com.zen.alchan.data.response.animethemes.AnimeResponse()
    return Anime(
        id = anime.id ?: 0,
        name = anime.name ?: "",
        slug = anime.slug ?: "",
        openings = anime.animeThemes?.filter { it.type == AnimeTheme.TYPE_OP }?.map { animeTheme ->
            AnimeTheme(
                id = animeTheme.id ?: 0,
                type = AnimeTheme.TYPE_OP,
                sequence = animeTheme.sequence ?: 0,
                group = animeTheme.group ?: "",
                slug = animeTheme.slug ?: "",
                song = AnimeThemeSong(
                    id = animeTheme.song?.id ?: 0,
                    title = animeTheme.song?.title ?: "",
                    artists = animeTheme.song?.artists?.map { artist ->
                        AnimeThemeSongArtist(
                            id = artist.id ?: 0,
                            name = artist.name ?: "",
                            slug = artist.slug ?: ""
                        )
                    } ?: listOf()
                ),
                themeEntries = animeTheme.themeEntries?.map { entry ->
                    AnimeThemeEntry(
                        id = entry.id ?: 0,
                        version = entry.version ?: 1,
                        episodes = entry.episodes ?: "",
                        nsfw = entry.nsfw ?: false,
                        spoiler = entry.spoiler ?: false,
                        videos = entry.videos?.map { video ->
                            AnimeThemeEntryVideo(
                                id = video.id ?: 0,
                                mimetype = video.mimetype ?: "",
                                resolution = video.resolution ?: 0,
                                nc = video.nc ?: false,
                                source = video.source ?: "",
                                link = video.link ?: "",
                                audio = AnimeThemeEntryVideoAudio(
                                    id = video.audio?.id ?: 0,
                                    mimetype = video.audio?.mimetype ?: "",
                                    link = video.audio?.link ?: ""
                                )
                            )
                        } ?: listOf()
                    )
                } ?: listOf()
            )
        } ?: listOf(),
        endings = anime.animeThemes?.filter { it.type == AnimeTheme.TYPE_ED }?.map { animeTheme ->
            AnimeTheme(
                id = animeTheme.id ?: 0,
                type = AnimeTheme.TYPE_ED,
                sequence = animeTheme.sequence ?: 0,
                group = animeTheme.group ?: "",
                slug = animeTheme.slug ?: "",
                song = AnimeThemeSong(
                    id = animeTheme.song?.id ?: 0,
                    title = animeTheme.song?.title ?: "",
                    artists = animeTheme.song?.artists?.map { artist ->
                        AnimeThemeSongArtist(
                            id = artist.id ?: 0,
                            name = artist.name ?: "",
                            slug = artist.slug ?: ""
                        )
                    } ?: listOf()
                ),
                themeEntries = animeTheme.themeEntries?.map { entry ->
                    AnimeThemeEntry(
                        id = entry.id ?: 0,
                        version = entry.version ?: 1,
                        episodes = entry.episodes ?: "",
                        nsfw = entry.nsfw ?: false,
                        spoiler = entry.spoiler ?: false,
                        videos = entry.videos?.map { video ->
                            AnimeThemeEntryVideo(
                                id = video.id ?: 0,
                                mimetype = video.mimetype ?: "",
                                resolution = video.resolution ?: 0,
                                nc = video.nc ?: false,
                                source = video.source ?: "",
                                link = video.link ?: "",
                                audio = AnimeThemeEntryVideoAudio(
                                    id = video.audio?.id ?: 0,
                                    mimetype = video.audio?.mimetype ?: "",
                                    link = video.audio?.link ?: ""
                                )
                            )
                        } ?: listOf()
                    )
                } ?: listOf()
            )
        } ?: listOf()
    )
}

fun AnimeResponse.convert(): Anime {
    return Anime(
        id = data?.malId ?: 0,
        name = data?.title ?: "",
        openings = data?.theme?.openings?.map { opening ->
            AnimeTheme(
                type = AnimeTheme.TYPE_OP,
                song = AnimeThemeSong(
                    title = opening
                )
            )
        } ?: listOf(),
        endings = data?.theme?.endings?.map { ending ->
            AnimeTheme(
                type = AnimeTheme.TYPE_ED,
                song = AnimeThemeSong(
                    title = ending
                )
            )
        } ?: listOf()
    )
}