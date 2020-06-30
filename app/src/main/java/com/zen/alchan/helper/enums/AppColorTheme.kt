package com.zen.alchan.helper.enums

import com.zen.alchan.R
import com.zen.alchan.helper.pojo.ColorPalette

enum class AppColorTheme(val value: ColorPalette) {
    YELLOW(ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)),
    GREEN(ColorPalette(R.color.green, R.color.lavender, R.color.brown)),
    BLUE(ColorPalette(R.color.blue, R.color.cream, R.color.gold)),
    PINK(ColorPalette(R.color.pink, R.color.sunshine, R.color.jade)),
    RED(ColorPalette(R.color.red, R.color.aloevera, R.color.purple))
}