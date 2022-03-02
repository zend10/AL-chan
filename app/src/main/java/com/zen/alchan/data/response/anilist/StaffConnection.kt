package com.zen.alchan.data.response.anilist

data class StaffConnection(
    val edges: List<StaffEdge> = listOf(),
    val nodes: List<Staff> = listOf(),
    val pageInfo: PageInfo = PageInfo()
)