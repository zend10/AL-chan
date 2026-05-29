package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ViewerResponse(
    @SerialName("Viewer")
    val viewer: UserResponse? = null
) {
    fun toModel(): User {
        return viewer?.toModel() ?: User()
    }
}
