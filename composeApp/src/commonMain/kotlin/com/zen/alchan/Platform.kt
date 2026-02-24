package com.zen.alchan

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform