package com.zen.shared.data.response



data class Anime(
    val id: Int = 0,
    val name: String = "",
    val slug: String = "",
    val openings: List<AnimeTheme> = listOf(),
    val endings: List<AnimeTheme> = listOf()
)

data class AnimeTheme(
    val id: Int = 0,
    val type: String = "",
    val sequence: Int = 0,
    val group: String = "",
    val slug: String = "",
    val song: AnimeThemeSong = AnimeThemeSong(),
    val themeEntries: List<AnimeThemeEntry> = listOf()
) {
    fun getDisplayTitle(): String {
        val titleStringBuilder = StringBuilder()

        if (themeEntries.isEmpty()) {
            titleStringBuilder.append(song.title)
        } else {
            if (sequence != 0)
                titleStringBuilder.append("#${sequence}: ")

            if (song.title != "")
                titleStringBuilder.append("\"${song.title}\"")

            if (song.artists.isNotEmpty())
                titleStringBuilder.append(" by ${song.artists.joinToString(", ") { it.name }}")

            if (themeEntries.size == 1 && themeEntries.first().episodes.isNotBlank())
                titleStringBuilder.append(" (Ep. ${themeEntries.first().episodes})")
        }

        return titleStringBuilder.toString()
    }

    companion object {
        const val TYPE_OP = "OP"
        const val TYPE_ED = "ED"
    }
}

data class AnimeThemeSong(
    val id: Int = 0,
    val title: String = "",
    val artists: List<AnimeThemeSongArtist> = listOf()
)

data class AnimeThemeSongArtist(
    val id: Int = 0,
    val name: String = "",
    val slug: String = ""
)

data class AnimeThemeEntry(
    val id: Int = 0,
    val version: Int = 1,
    val episodes: String = "",
    val nsfw: Boolean = false,
    val spoiler: Boolean = false,
    val videos: List<AnimeThemeEntryVideo> = listOf()
) {
    fun getDisplayTitle(): String {
        return "v${version}${if (episodes != "") " (Ep. ${episodes})" else ""}"
    }
}

data class AnimeThemeEntryVideo(
    val id: Int = 0,
    val mimetype: String = "",
    val resolution: Int = 0,
    val nc: Boolean = false,
    val source: String = "",
    val link: String = "",
    val audio: AnimeThemeEntryVideoAudio = AnimeThemeEntryVideoAudio()
)

data class AnimeThemeEntryVideoAudio(
    val id: Int = 0,
    val mimetype: String = "",
    val link: String = ""
)