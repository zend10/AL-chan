package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val id: Int = 0,
    val idMal: Int = 0,
    val title: MediaTitle = MediaTitle()
)