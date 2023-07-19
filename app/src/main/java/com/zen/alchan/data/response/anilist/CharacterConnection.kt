package com.zen.alchan.data.response.anilist

data class CharacterConnection(
    val edges: List<CharacterEdge> = listOf(),
    val nodes: List<Character> = listOf(),
    val pageInfo: PageInfo = PageInfo()
)