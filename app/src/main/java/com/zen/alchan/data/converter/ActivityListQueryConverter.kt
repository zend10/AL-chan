package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

fun ActivityListQuery.Data.convert(): Page<Activity> {
    return Page(
        pageInfo = PageInfo(
            total = page?.pageInfo?.total ?: 0,
            perPage = page?.pageInfo?.perPage ?: 0,
            currentPage = page?.pageInfo?.currentPage ?: 0,
            lastPage = page?.pageInfo?.lastPage ?: 0,
            hasNextPage = page?.pageInfo?.hasNextPage ?: false
        ),
        data = page?.activities?.filterNotNull()?.map {
            when (it.__typename) {
                TextActivity::class.java.simpleName -> {
                    it.fragments.onTextActivity?.convert() ?: TextActivity()
                }
                ListActivity::class.java.simpleName -> {
                    it.fragments.onListActivity?.convert() ?: ListActivity()
                }
                MessageActivity::class.java.simpleName -> {
                    it.fragments.onMessageActivity?.convert() ?: MessageActivity()
                }
                else -> TextActivity()
            }
        } ?: listOf()
    )
}