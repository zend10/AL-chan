package com.zen.alchan.data.localstorage

class InfoManagerImpl(private val localStorage: LocalStorage) : InfoManager {
    
    override val lastAnnouncementId: Int?
        get() = localStorage.lastAnnouncemendId

    override fun setLastAnnouncementId(value: Int) {
        localStorage.lastAnnouncemendId = value
    }
}