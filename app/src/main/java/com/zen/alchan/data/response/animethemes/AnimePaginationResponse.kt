package com.zen.alchan.data.response.animethemes

import com.google.gson.annotations.SerializedName

data class AnimePaginationResponse(
    @SerializedName("anime")
    val anime: List<AnimeResponse>? = null
)

data class AnimeResponse(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("animethemes")
    val animeThemes: List<AnimeThemeResponse>? = null
)

data class AnimeThemeResponse(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("sequence")
    val sequence: Int? = null,
    @SerializedName("group")
    val group: String? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("song")
    val song: AnimeThemeSongResponse? = null,
    @SerializedName("animethemeentries")
    val themeEntries: List<AnimeThemeEntryResponse>? = null
)

data class AnimeThemeSongResponse(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("artists")
    val artists: List<AnimeThemeSongArtistResponse>? = null
)

data class AnimeThemeSongArtistResponse(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("slug")
    val slug: String? = null
)

data class AnimeThemeEntryResponse(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("version")
    val version: Int? = null,
    @SerializedName("episodes")
    val episodes: String? = null,
    @SerializedName("nsfw")
    val nsfw: Boolean? = null,
    @SerializedName("spoiler")
    val spoiler: Boolean? = null,
    @SerializedName("videos")
    val videos: List<AnimeThemeEntryVideoResponse>? = null
)

data class AnimeThemeEntryVideoResponse(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("mimetype")
    val mimetype: String? = null,
    @SerializedName("resolution")
    val resolution: Int? = null,
    @SerializedName("nc")
    val nc: Boolean? = null,
    @SerializedName("source")
    val source: String? = null,
    @SerializedName("link")
    val link: String? = null,
    @SerializedName("audio")
    val audio: AnimeThemeEntryVideoAudioResponse? = null
)

data class AnimeThemeEntryVideoAudioResponse(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("mimetype")
    val mimetype: String? = null,
    @SerializedName("link")
    val link: String? = null
)
