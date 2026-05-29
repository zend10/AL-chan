package com.zen.alchan.data.enums

enum class ModRole {
    ADMIN,
    LEAD_DEVELOPER,
    DEVELOPER,
    LEAD_COMMUNITY,
    COMMUNITY,
    DISCORD_COMMUNITY,
    LEAD_ANIME_DATA,
    ANIME_DATA,
    LEAD_MANGA_DATA,
    MANGA_DATA,
    LEAD_SOCIAL_MEDIA,
    SOCIAL_MEDIA,
    RETIRED,
    CHARACTER_DATA,
    STAFF_DATA
}

fun getModRoleEnum(modRole: String?): ModRole? {
    return ModRole.entries.firstOrNull { it.name.equals(modRole, true) }
}
