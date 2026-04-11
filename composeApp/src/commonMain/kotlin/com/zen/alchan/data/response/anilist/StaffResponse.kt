package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class StaffResponse(
    val id: Int? = null,
    val name: StaffNameResponse? = null
)
