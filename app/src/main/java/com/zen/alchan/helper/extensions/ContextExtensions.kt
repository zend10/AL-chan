package com.zen.alchan.helper.extensions

import android.content.Context
import android.util.TypedValue

fun Context.getAttrValue(attrResId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrResId, typedValue, true)
    return typedValue.data
}