package com.zen.alchan.data.localstorage

class InfoManagerImpl(private val localStorage: LocalStorage) : InfoManager {
    
    override val lastAnnouncementId: Int?
        get() = localStorage.lastAnnouncementId

    override fun setLastAnnouncementId(value: Int) {
        localStorage.lastAnnouncementId = value
    }
}