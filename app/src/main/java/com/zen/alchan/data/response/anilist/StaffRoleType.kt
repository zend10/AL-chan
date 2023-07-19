package com.zen.alchan.data.response.anilist

data class StaffRoleType(
    val voiceActor: Staff = Staff(),
    val roleNote: String = "",
    val dubGroup: String = ""
)