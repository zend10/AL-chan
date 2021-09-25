package com.zen.alchan.data.entitiy

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.extensions.*

data class ListStyle(
    var listType: ListType = ListType.LINEAR,
    var longPressShowDetail: Boolean = true,
    var hideMediaFormat: Boolean = false,
    var hideScoreWhenNotScored: Boolean = false,
    var hideVolumeForManga: Boolean = false,
    var hideChapterForManga: Boolean = false,
    var hideVolumeForNovel: Boolean = false,
    var hideChapterForNovel: Boolean = false,
    var hideAiring: Boolean = false,
    var showNotes: Boolean = false,
    var showPriority: Boolean = false,
    var primaryColor: String? = null,
    var secondaryColor: String? = null,
    var textColor: String? = null,
    var cardColor: String? = null,
    var toolbarColor: String? = null,
    var backgroundColor: String? = null,
    var floatingButtonColor: String? = null,
    var floatingIconColor: String? = null
) {
    @ColorInt
    fun getPrimaryColor(context: Context): Int {
        return if (primaryColor != null)
            Color.parseColor(primaryColor)
        else
            context.getThemePrimaryColor()
    }

    @ColorInt
    fun getSecondaryColor(context: Context): Int {
        return if (secondaryColor != null)
            Color.parseColor(secondaryColor)
        else
            context.getThemeSecondaryColor()
    }

    @ColorInt
    fun getTextColor(context: Context): Int {
        return if (textColor != null)
            Color.parseColor(textColor)
        else
            context.getThemeTextColor()
    }

    @ColorInt
    fun getCardColor(context: Context): Int {
        return if (cardColor != null)
            Color.parseColor(cardColor)
        else
            context.getThemeCardColor()
    }

    @ColorInt
    fun getToolbarColor(context: Context): Int {
        return if (toolbarColor != null)
            Color.parseColor(toolbarColor)
        else
            context.getThemeToolbarColor()
    }

    @ColorInt
    fun getBackgroundColor(context: Context): Int {
        return if (backgroundColor != null)
            Color.parseColor(backgroundColor)
        else
            context.getThemeBackgroundColor()
    }

    @ColorInt
    fun getFloatingButtonColor(context: Context): Int {
        return if (floatingButtonColor != null)
            Color.parseColor(floatingButtonColor)
        else
            context.getThemeFloatingButtonColor()
    }

    @ColorInt
    fun getFloatingIconColor(context: Context): Int {
        return if (floatingIconColor != null)
            Color.parseColor(floatingIconColor)
        else
            context.getThemeFloatingIconColor()
    }
}