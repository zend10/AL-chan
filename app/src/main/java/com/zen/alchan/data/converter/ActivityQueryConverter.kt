package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.ListActivity
import com.zen.alchan.data.response.anilist.MessageActivity
import com.zen.alchan.data.response.anilist.TextActivity

fun ActivityQuery.Data.convert(): Activity {
    return when (activity?.__typename) {
        TextActivity::class.java.simpleName -> {
            activity?.fragments?.onTextActivity?.convert() ?: TextActivity()
        }
        ListActivity::class.java.simpleName -> {
            activity?.fragments?.onListActivity?.convert() ?: ListActivity()
        }
        MessageActivity::class.java.simpleName -> {
            activity?.fragments?.onMessageActivity?.convert() ?: MessageActivity()
        }
        else -> TextActivity()
    }
}