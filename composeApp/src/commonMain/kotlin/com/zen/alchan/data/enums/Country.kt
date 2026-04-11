package com.zen.alchan.data.enums

enum class Country(val code: String) {
    JAPAN("JP"),
    SOUTH_KOREA("KR"),
    CHINA("CN"),
    TAIWAN("TW")
}

fun getCountryEnum(countryCode: String?): Country? {
    return Country.entries.firstOrNull { it.code.equals(countryCode, true) }
}