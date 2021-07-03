package com.zen.alchan.helper.pojo

data class ListItem<T>(
    val text: String,
    val stringResources: List<Int> = listOf(),
    val data: T
)