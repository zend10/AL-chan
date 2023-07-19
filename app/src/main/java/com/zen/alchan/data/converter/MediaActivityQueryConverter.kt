package com.zen.alchan.data.converter

import com.zen.alchan.MediaActivityQuery
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.ListActivity
import com.zen.alchan.data.response.anilist.Page
import com.zen.alchan.data.response.anilist.PageInfo

fun MediaActivityQuery.Data.convert(): Page<ListActivity> {
    return Page(
        pageInfo = PageInfo(
            total = Page?.pageInfo?.total ?: 0,
            perPage = Page?.pageInfo?.perPage ?: 0,
            currentPage = Page?.pageInfo?.currentPage ?: 0,
            lastPage = Page?.pageInfo?.lastPage ?: 0,
            hasNextPage = Page?.pageInfo?.hasNextPage ?: false
        ),
        data = Page?.activities?.filterNotNull()?.map { activity ->
            activity.onListActivity?.convert() ?: ListActivity()
        } ?: listOf()
    )
}