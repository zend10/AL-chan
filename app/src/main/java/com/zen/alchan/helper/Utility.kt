package com.zen.alchan.helper

import android.content.Context
import android.util.TypedValue
import com.zen.alchan.R

object Utility {

    fun getResValueFromRefAttr(context: Context, attrResId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrResId, typedValue, true)
        return typedValue.data
    }
}