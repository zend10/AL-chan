package com.zen.alchan.data.response.anilist

import type.CharacterRole

data class CharacterEdge(
    val node: Character = Character(),
    val role: CharacterRole? = null,
    val name: String = ""
)