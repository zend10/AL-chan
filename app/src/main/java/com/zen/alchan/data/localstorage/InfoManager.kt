package com.zen.alchan.data.localstorage

interface InfoManager {
    val lastAnnouncementId: Int?

    fun setLastAnnouncementId(value: Int)
}