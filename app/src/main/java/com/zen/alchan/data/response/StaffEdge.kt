package com.zen.alchan.data.response

data class StaffEdge(
    val node: Staff = Staff(),
    val id: Int = 0,
    val role: String = "",
    val favouriteOrder: Int = 0
)