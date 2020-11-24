package com.zen.alchan.helper.enums

import com.zen.alchan.R
import com.zen.alchan.helper.pojo.ColorPalette

enum class AppColorTheme(val value: ColorPalette) {
    DEFAULT_THEME_YELLOW(ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)),
    DEFAULT_THEME_GREEN(ColorPalette(R.color.green, R.color.lavender, R.color.brown)),
    DEFAULT_THEME_BLUE(ColorPalette(R.color.blue, R.color.cream, R.color.gold)),
    DEFAULT_THEME_PINK(ColorPalette(R.color.pink, R.color.sunshine, R.color.jade)),
    DEFAULT_THEME_RED(ColorPalette(R.color.red, R.color.aloevera, R.color.purple)),
    LIGHT_THEME_YELLOW(ColorPalette(R.color.lightThemeYellow, R.color.lightThemeCyan, R.color.lightThemeMagentaDark)),
    LIGHT_THEME_GREEN(ColorPalette(R.color.lightGreen, R.color.lightLavender, R.color.lightBrown)),
    LIGHT_THEME_BLUE(ColorPalette(R.color.lightBlue, R.color.lightCream, R.color.lightGold)),
    LIGHT_THEME_PINK(ColorPalette(R.color.lightPink, R.color.lightSunshine, R.color.lightJade)),
    LIGHT_THEME_RED(ColorPalette(R.color.lightRed, R.color.lightAloevera, R.color.lightPurple)),
    DARK_THEME_YELLOW(ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)),
    DARK_THEME_GREEN(ColorPalette(R.color.green, R.color.lavender, R.color.brown)),
    DARK_THEME_BLUE(ColorPalette(R.color.blue, R.color.cream, R.color.gold)),
    DARK_THEME_PINK(ColorPalette(R.color.pink, R.color.sunshine, R.color.jade)),
    DARK_THEME_RED(ColorPalette(R.color.red, R.color.aloevera, R.color.purple)),
    ANILIST_LIGHT_BLUE(ColorPalette(R.color.anilistBlue, R.color.lightBlue, R.color.anilistBrightRed)),
    ANILIST_LIGHT_PURPLE(ColorPalette(R.color.anilistPurple, R.color.anilistLightPurple, R.color.anilistPaleOrange)),
    ANILIST_LIGHT_GREEN(ColorPalette(R.color.anilistGreen, R.color.anilistLightGreen, R.color.anilistDarkPurple)),
    ANILIST_LIGHT_ORANGE(ColorPalette(R.color.anilistOrange, R.color.anilistLightOrange, R.color.anilistBrightGreen)),
    ANILIST_LIGHT_RED(ColorPalette(R.color.anilistRed, R.color.anilistLightRed, R.color.anilistBrightYellow)),
    ANILIST_LIGHT_PINK(ColorPalette(R.color.anilistPink, R.color.anilistBrightPink, R.color.anilistCyan)),
    ANILIST_LIGHT_GREY(ColorPalette(R.color.anilistGrey, R.color.anilistBrightGrey, R.color.anilistBrown)),
    ANILIST_DARK_BLUE(ColorPalette(R.color.anilistBlue, R.color.lightBlue, R.color.anilistBrightRed)),
    ANILIST_DARK_PURPLE(ColorPalette(R.color.anilistPurple, R.color.anilistLightPurple, R.color.anilistPaleOrange)),
    ANILIST_DARK_GREEN(ColorPalette(R.color.anilistGreen, R.color.anilistLightGreen, R.color.anilistDarkPurple)),
    ANILIST_DARK_ORANGE(ColorPalette(R.color.anilistOrange, R.color.anilistLightOrange, R.color.anilistBrightGreen)),
    ANILIST_DARK_RED(ColorPalette(R.color.anilistRed, R.color.anilistLightRed, R.color.anilistBrightYellow)),
    ANILIST_DARK_PINK(ColorPalette(R.color.anilistPink, R.color.anilistBrightPink, R.color.anilistCyan)),
    ANILIST_DARK_GREY(ColorPalette(R.color.anilistGrey, R.color.anilistBrightGrey, R.color.anilistBrown)),
    COMMUNITY_AXIEL_BLUE(ColorPalette(R.color.axielBlue, R.color.axielLightBlue, R.color.axielRed)),
    COMMUNITY_SAM_ORANGE(ColorPalette(R.color.samOrange, R.color.samGreen, R.color.samBlue)),
    COMMUNITY_DARK_AXIEL_BLUE(ColorPalette(R.color.axielBlue, R.color.axielLightBlue, R.color.axielRed)),
    COMMUNITY_DARK_SAM_ORANGE(ColorPalette(R.color.samOrange, R.color.samGreen, R.color.samBlue))
}