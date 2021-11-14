package com.zen.alchan.data.response.anilist

data class MediaConnection(
    val edges: List<MediaEdge> = listOf(),
    val nodes: List<Media> = listOf(),
    val pageInfo: PageInfo = PageInfo()
)