package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class Staff(
    val id: String = "",
    val name: StaffName = StaffName(),
    val role: String = ""
)