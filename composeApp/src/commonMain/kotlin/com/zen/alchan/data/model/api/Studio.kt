package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class Studio(
    val id: String = "",
    val name: String = "",
    val isMain: Boolean = false
)
