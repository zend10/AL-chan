package com.zen.alchan.data.response.anilist

data class StudioConnection(
    val edges: List<StudioEdge> = listOf(),
    val nodes: List<Studio> = listOf()
)