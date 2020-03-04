package com.zen.alchan.helper.utils

import android.content.Context
import android.util.TypedValue
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.ColorPalette
import type.ScoreFormat

object AndroidUtility {

    fun getResValueFromRefAttr(context: Context?, attrResId: Int): Int {
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(attrResId, typedValue, true)
        return typedValue.data
    }

    fun getAppColorThemeRes(appColorTheme: AppColorTheme?): Int {
        return when (appColorTheme ?: Constant.DEFAULT_THEME) {
            AppColorTheme.YELLOW -> R.style.AppTheme_ThemeYellow
            AppColorTheme.GREEN -> R.style.AppTheme_ThemeGreen
            AppColorTheme.BLUE -> R.style.AppTheme_ThemeBlue
            AppColorTheme.PINK -> R.style.AppTheme_ThemePink
            AppColorTheme.RED -> R.style.AppTheme_ThemeRed
        }
    }

    fun getColorPalette(appColorTheme: AppColorTheme?): ColorPalette {
        return when (appColorTheme ?: Constant.DEFAULT_THEME) {
            AppColorTheme.YELLOW -> ColorPalette(R.color.yellow, R.color.cyan, R.color.magentaDark)
            AppColorTheme.GREEN -> ColorPalette(R.color.green, R.color.lavender, R.color.brown)
            AppColorTheme.BLUE -> ColorPalette(R.color.blue, R.color.cream, R.color.gold)
            AppColorTheme.PINK -> ColorPalette(R.color.pink, R.color.sunshine, R.color.jade)
            AppColorTheme.RED -> ColorPalette(R.color.red, R.color.aloevera, R.color.purple)
        }
    }

    fun getSmileyFromScore(score: Double?): Int? {
        return when (score) {
            1.0 -> R.drawable.ic_sad
            2.0 -> R.drawable.ic_neutral
            3.0 -> R.drawable.ic_happy
            else -> R.drawable.ic_puzzled
        }
    }
}