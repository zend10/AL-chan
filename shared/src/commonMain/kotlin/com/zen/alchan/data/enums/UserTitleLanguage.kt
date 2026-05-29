package com.zen.alchan.data.enums

enum class UserTitleLanguage {
    ROMAJI,
    ENGLISH,
    NATIVE,
    ROMAJI_STYLISED,
    ENGLISH_STYLISED,
    NATIVE_STYLISED
}

fun getUserTitleLanguageEnum(userTitleLanguage: String?): UserTitleLanguage? {
    return UserTitleLanguage.entries.firstOrNull { it.name.equals(userTitleLanguage, true) }
}
