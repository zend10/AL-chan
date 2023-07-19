package com.zen.alchan.data.converter

import com.zen.alchan.ActivityListQuery
import com.zen.alchan.data.response.anilist.*

fun ActivityListQuery.Data.convert(): Page<Activity> {
    return Page(
        pageInfo = PageInfo(
            total = Page?.pageInfo?.total ?: 0,
            perPage = Page?.pageInfo?.perPage ?: 0,
            currentPage = Page?.pageInfo?.currentPage ?: 0,
            lastPage = Page?.pageInfo?.lastPage ?: 0,
            hasNextPage = Page?.pageInfo?.hasNextPage ?: false
        ),
        data = Page?.activities?.filterNotNull()?.map {
            when (it.__typename) {
                "TextActivity" -> {
                    it.onTextActivity?.convert() ?: TextActivity()
                }
                "ListActivity" -> {
                    it.onListActivity?.convert() ?: ListActivity()
                }
                "MessageActivity" -> {
                    it.onMessageActivity?.convert() ?: MessageActivity()
                }
                else -> TextActivity()
            }
        } ?: listOf()
    )
}