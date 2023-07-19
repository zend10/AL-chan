package com.zen.alchan.data.response.anilist

import com.zen.alchan.helper.enums.Favorite

data class Favourites(
    val anime: MediaConnection = MediaConnection(),
    val manga: MediaConnection = MediaConnection(),
    val characters: CharacterConnection = CharacterConnection(),
    val staff: StaffConnection = StaffConnection(),
    val studios: StudioConnection = StudioConnection()
) {
    fun getPageInfo(favorite: Favorite): PageInfo {
        return when (favorite) {
            Favorite.ANIME -> anime.pageInfo
            Favorite.MANGA -> manga.pageInfo
            Favorite.CHARACTERS -> characters.pageInfo
            Favorite.STAFF -> staff.pageInfo
            Favorite.STUDIOS -> studios.pageInfo
        }
    }
}