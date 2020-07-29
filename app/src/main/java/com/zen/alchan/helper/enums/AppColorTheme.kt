package com.zen.alchan.helper.enums

import com.zen.alchan.R
import com.zen.alchan.helper.pojo.ColorPalette

enum class AppColorTheme(val value: ColorPalette) {
    YELLOW(ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)),
    GREEN(ColorPalette(R.color.green, R.color.lavender, R.color.brown)),
    BLUE(ColorPalette(R.color.blue, R.color.cream, R.color.gold)),
    PINK(ColorPalette(R.color.pink, R.color.sunshine, R.color.jade)),
    RED(ColorPalette(R.color.red, R.color.aloevera, R.color.purple)),
    LIGHT_YELLOW(ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)),
    LIGHT_GREEN(ColorPalette(R.color.green, R.color.lavender, R.color.brown)),
    LIGHT_BLUE(ColorPalette(R.color.blue, R.color.cream, R.color.gold)),
    LIGHT_PINK(ColorPalette(R.color.pink, R.color.sunshine, R.color.jade)),
    LIGHT_RED(ColorPalette(R.color.red, R.color.aloevera, R.color.purple)),
    DARK_YELLOW(ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)),
    DARK_GREEN(ColorPalette(R.color.green, R.color.lavender, R.color.brown)),
    DARK_BLUE(ColorPalette(R.color.blue, R.color.cream, R.color.gold)),
    DARK_PINK(ColorPalette(R.color.pink, R.color.sunshine, R.color.jade)),
    DARK_RED(ColorPalette(R.color.red, R.color.aloevera, R.color.purple)),
}