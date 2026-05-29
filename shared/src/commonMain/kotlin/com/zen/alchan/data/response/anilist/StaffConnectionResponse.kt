package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.Staff
import kotlinx.serialization.Serializable

@Serializable
data class StaffConnectionResponse(
    val edges: List<StaffEdgeResponse>? = null
) {
    fun toModel(): List<Staff> {
        return edges?.mapNotNull {
            it.node?.toModel(role = it.role ?: "")
        } ?: listOf()
    }
}

@Serializable
data class StaffEdgeResponse(
    val node: StaffResponse? = null,
    val role: String? = null
)
