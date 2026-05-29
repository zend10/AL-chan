package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.Staff
import com.zen.alchan.data.model.api.StaffName
import kotlinx.serialization.Serializable

@Serializable
data class StaffResponse(
    val id: Int? = null,
    val name: StaffNameResponse? = null
) {
    fun toModel(role: String = ""): Staff {
        return Staff(
            id = id?.toString() ?: "",
            name = name?.toModel() ?: StaffName(),
            role = role
        )
    }
}
