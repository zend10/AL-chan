package com.zen.alchan.helper.pojo

class ColorPalette(
    val primaryColor: Int,
    val secondaryColor: Int,
    val negativeColor: Int
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val colorPalette = other as ColorPalette
        return (
            primaryColor == colorPalette.primaryColor &&
            secondaryColor == colorPalette.secondaryColor &&
            negativeColor == colorPalette.negativeColor
        )
    }
}