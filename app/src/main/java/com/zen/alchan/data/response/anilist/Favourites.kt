package com.zen.alchan.data.response.anilist

data class Favourites(
    val anime: MediaConnection = MediaConnection(),
    val manga: MediaConnection = MediaConnection(),
    val characters: CharacterConnection = CharacterConnection(),
    val staff: StaffConnection = StaffConnection(),
    val studios: StudioConnection = StudioConnection()
)