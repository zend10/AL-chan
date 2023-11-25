package com.zen.alchan.helper.enums

import android.content.Context
import com.zen.R

enum class ListOrder(val value: String) {
    SCORE("score"),
    TITLE("title"),
    LAST_UPDATED("updatedAt"),
    LAST_ADDED("id")
}

fun ListOrder.getString(context: Context): String {
    return context.getString(getStringResource())
}

fun ListOrder.getStringResource(): Int {
    return when (this) {
        ListOrder.SCORE -> R.string.score
        ListOrder.TITLE -> R.string.title
        ListOrder.LAST_UPDATED -> R.string.last_updated
        ListOrder.LAST_ADDED -> R.string.last_added
    }
}