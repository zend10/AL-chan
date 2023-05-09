package com.zen.alchan.data.converter

import com.zen.alchan.ActivityQuery
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.ListActivity
import com.zen.alchan.data.response.anilist.MessageActivity
import com.zen.alchan.data.response.anilist.TextActivity

fun ActivityQuery.Data.convert(): Activity {
    return when (Activity?.__typename) {
        TextActivity::class.java.simpleName -> {
            Activity?.onTextActivity?.convert() ?: TextActivity()
        }
        ListActivity::class.java.simpleName -> {
            Activity?.onListActivity?.convert() ?: ListActivity()
        }
        MessageActivity::class.java.simpleName -> {
            Activity?.onMessageActivity?.convert() ?: MessageActivity()
        }
        else -> TextActivity()
    }
}