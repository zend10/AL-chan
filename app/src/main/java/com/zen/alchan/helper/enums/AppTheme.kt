package com.zen.alchan.helper.enums

import com.zen.alchan.R
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import java.util.*

enum class AppTheme(val colors: Triple<Int, Int, Int>) {
    DEFAULT_THEME_YELLOW(Triple(R.color.yellow, R.color.cyan, R.color.magentaDark)),
    DEFAULT_THEME_BLUE(Triple(R.color.blue, R.color.cream, R.color.gold)),
    DEFAULT_THEME_PURPLE(Triple(R.color.white, R.color.white, R.color.white)),
    DEFAULT_THEME_GREEN(Triple(R.color.green, R.color.lavender, R.color.brown)),
    DEFAULT_THEME_ORANGE(Triple(R.color.white, R.color.white, R.color.white)),
    DEFAULT_THEME_RED(Triple(R.color.red, R.color.aloevera, R.color.purple)),
    DEFAULT_THEME_PINK(Triple(R.color.pink, R.color.sunshine, R.color.jade)),
    LIGHT_THEME_BLUE(Triple(R.color.lightBlue, R.color.lightCream, R.color.lightGold)),
    LIGHT_THEME_PURPLE(Triple(R.color.black, R.color.black, R.color.black)),
    LIGHT_THEME_GREEN(Triple(R.color.lightGreen, R.color.lightLavender, R.color.lightBrown)),
    LIGHT_THEME_ORANGE(Triple(R.color.lightThemeYellow, R.color.lightThemeCyan, R.color.lightThemeMagentaDark)),
    LIGHT_THEME_RED(Triple(R.color.lightRed, R.color.lightAloevera, R.color.lightPurple)),
    LIGHT_THEME_PINK(Triple(R.color.lightPink, R.color.lightSunshine, R.color.lightJade)),
    DARK_THEME_YELLOW(Triple(R.color.yellow, R.color.cyan, R.color.magentaDark)),
    DARK_THEME_BLUE(Triple(R.color.blue, R.color.cream, R.color.gold)),
    DARK_THEME_PURPLE(Triple(R.color.white, R.color.white, R.color.white)),
    DARK_THEME_GREEN(Triple(R.color.green, R.color.lavender, R.color.brown)),
    DARK_THEME_ORANGE(Triple(R.color.white, R.color.white, R.color.white)),
    DARK_THEME_RED(Triple(R.color.red, R.color.aloevera, R.color.purple)),
    DARK_THEME_PINK(Triple(R.color.pink, R.color.sunshine, R.color.jade)),
    ANILIST_LIGHT_BLUE(Triple(R.color.anilistBlue, R.color.anilistLightBlue, R.color.anilistBrightRed)),
    ANILIST_LIGHT_PURPLE(Triple(R.color.anilistPurple, R.color.anilistLightPurple, R.color.anilistPaleOrange)),
    ANILIST_LIGHT_GREEN(Triple(R.color.anilistGreen, R.color.anilistLightGreen, R.color.anilistDarkPurple)),
    ANILIST_LIGHT_ORANGE(Triple(R.color.anilistOrange, R.color.anilistLightOrange, R.color.anilistBrightGreen)),
    ANILIST_LIGHT_RED(Triple(R.color.anilistRed, R.color.anilistLightRed, R.color.anilistBrightYellow)),
    ANILIST_LIGHT_PINK(Triple(R.color.anilistPink, R.color.anilistBrightPink, R.color.anilistCyan)),
    ANILIST_LIGHT_GREY(Triple(R.color.anilistGrey, R.color.anilistBrightGrey, R.color.anilistBrown)),
    ANILIST_DARK_BLUE(Triple(R.color.anilistBlue, R.color.anilistLightBlue, R.color.anilistBrightRed)),
    ANILIST_DARK_PURPLE(Triple(R.color.anilistPurple, R.color.anilistLightPurple, R.color.anilistPaleOrange)),
    ANILIST_DARK_GREEN(Triple(R.color.anilistGreen, R.color.anilistLightGreen, R.color.anilistDarkPurple)),
    ANILIST_DARK_ORANGE(Triple(R.color.anilistOrange, R.color.anilistLightOrange, R.color.anilistBrightGreen)),
    ANILIST_DARK_RED(Triple(R.color.anilistRed, R.color.anilistLightRed, R.color.anilistBrightYellow)),
    ANILIST_DARK_PINK(Triple(R.color.anilistPink, R.color.anilistBrightPink, R.color.anilistCyan)),
    ANILIST_DARK_GREY(Triple(R.color.anilistGrey, R.color.anilistBrightGrey, R.color.anilistBrown))
}

fun AppTheme.getColorName(): String {
    return this.name.split("_").last().lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun AppTheme.getString(): String {
    return name.convertFromSnakeCase()
}