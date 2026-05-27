package com.zen.alchan.data.enums

enum class UserStaffNameLanguage {
    ROMAJI_WESTERN,
    ROMAJI,
    NATIVE
}

fun getUserStaffNameLanguageEnum(userStaffNameLanguage: String?): UserStaffNameLanguage? {
    return UserStaffNameLanguage.entries.firstOrNull { it.name.equals(userStaffNameLanguage, true) }
}
