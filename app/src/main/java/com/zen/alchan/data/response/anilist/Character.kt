package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entitiy.AppSetting

data class Character(
    val id: Int = 0,
    val name: CharacterName = CharacterName(),
    val image: CharacterImage = CharacterImage(),
    val description: String = "",
    val gender: String = "",
    val dateOfBirth: FuzzyDate? = null,
    val age: String = "",
    val bloodType: String = "",
    val isFavourite: Boolean = false,
    val siteUrl: String = "",
    val media: MediaConnection = MediaConnection(),
    val favourites: Int = 0
) {
    fun getImage(appSetting: AppSetting): String {
        return if (appSetting.useHighestQualityImage)
            image.large
        else
            image.medium
    }
}