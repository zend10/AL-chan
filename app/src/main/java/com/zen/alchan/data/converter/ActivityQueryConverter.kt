package com.zen.alchan.data.converter

import com.zen.alchan.ActivityQuery
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.ListActivity
import com.zen.alchan.data.response.anilist.MessageActivity
import com.zen.alchan.data.response.anilist.TextActivity

fun ActivityQuery.Data.convert(): Activity {
    return when (Activity?.__typename) {
        "TextActivity" -> {
            Activity?.onTextActivity?.convert() ?: TextActivity()
        }
        "ListActivity" -> {
            Activity?.onListActivity?.convert() ?: ListActivity()
        }
        "MessageActivity" -> {
            Activity?.onMessageActivity?.convert() ?: MessageActivity()
        }
        else -> TextActivity()
    }
}