package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

fun MediaStaffQuery.Data.convert(): StaffConnection {
    val staffConnection = media?.staff ?: return StaffConnection()

    return StaffConnection(
        edges = staffConnection.edges?.map {
            StaffEdge(
                node = Staff(
                    id = it?.node?.id ?: 0,
                    name = StaffName(
                        first = it?.node?.name?.first ?: "",
                        middle = it?.node?.name?.middle ?: "",
                        last = it?.node?.name?.last ?: "",
                        full = it?.node?.name?.full ?: "",
                        native = it?.node?.name?.native_ ?: "",
                        alternative = it?.node?.name?.alternative?.filterNotNull() ?: listOf(),
                        userPreferred = it?.node?.name?.userPreferred ?: ""
                    ),
                    image = StaffImage(
                        large = it?.node?.image?.large ?: "",
                        medium = it?.node?.image?.medium ?: ""
                    )
                ),
                role = it?.role ?: ""
            )
        } ?: listOf(),
        pageInfo = PageInfo(
            total = staffConnection.pageInfo?.total ?: 0,
            perPage = staffConnection.pageInfo?.perPage ?: 0,
            currentPage = staffConnection.pageInfo?.currentPage ?: 0,
            lastPage = staffConnection.pageInfo?.lastPage ?: 0,
            hasNextPage = staffConnection.pageInfo?.hasNextPage ?: false
        )
    )
}