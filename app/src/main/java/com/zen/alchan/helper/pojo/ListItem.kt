package com.zen.alchan.helper.pojo

data class ListItem<T>(
    val text: String = "",
    val stringResources: List<Int> = listOf(),
    val data: T
) {
    constructor(stringResource: Int, data: T) : this("{0}", listOf(stringResource), data)
    constructor(text: String, data: T) : this(text, listOf(), data)
}