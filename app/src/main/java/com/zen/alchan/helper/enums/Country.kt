package com.zen.alchan.helper.enums

import com.zen.alchan.helper.extensions.convertFromSnakeCase

enum class Country(val iso: String) {
    JAPAN("JP"),
    SOUTH_KOREA("KR"),
    CHINA("CN"),
    TAIWAN("TW")
}

fun Country.getCountryName(): String {
    return this.name.convertFromSnakeCase(true)
}
