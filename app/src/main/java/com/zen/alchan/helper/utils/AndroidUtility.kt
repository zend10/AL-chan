package com.zen.alchan.helper.utils

import android.content.Context
import android.util.TypedValue
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppColorTheme

object AndroidUtility {

    fun getResValueFromRefAttr(context: Context?, attrResId: Int): Int {
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(attrResId, typedValue, true)
        return typedValue.data
    }

    fun getAppColorThemeRes(appColorTheme: AppColorTheme): Int {
        return when (appColorTheme) {
            AppColorTheme.YELLOW -> R.style.AppTheme_ThemeYellow
            AppColorTheme.CYAN -> R.style.AppTheme_ThemeCyan
        }
    }

}