package com.zen.alchan.data.model

data class User(
    val id: String = "",
    val name: String = "",
) {
    fun isGuest(): Boolean {
        return id.isBlank()
    }
}
