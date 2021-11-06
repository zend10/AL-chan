package com.zen.alchan.data.response.anilist

data class Character(
    val id: Int = 0,
    val name: CharacterName = CharacterName(),
    val image: CharacterImage = CharacterImage(),
    val description: String = "",
    val siteUrl: String = ""
)