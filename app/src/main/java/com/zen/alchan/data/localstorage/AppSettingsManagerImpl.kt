package com.zen.alchan.data.localstorage

import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.pojo.AppSettings
import com.zen.alchan.helper.pojo.UserPreferences
import type.MediaSort
import type.StaffLanguage

class AppSettingsManagerImpl(private val localStorage: LocalStorage) : AppSettingsManager {

    override val appSettings: AppSettings
        get() {
            val savedSettings = localStorage.appSettings
            if (savedSettings.appTheme == null) savedSettings.appTheme = AppColorTheme.DEFAULT_THEME_YELLOW
            if (savedSettings.circularAvatar == null) savedSettings.circularAvatar = true
            if (savedSettings.whiteBackgroundAvatar == null) savedSettings.whiteBackgroundAvatar = true
            if (savedSettings.showRecentReviews == null) savedSettings.showRecentReviews = true
            if (savedSettings.useRelativeDate == null) savedSettings.useRelativeDate = false
            if (savedSettings.sendAiringPushNotification == null) savedSettings.sendAiringPushNotification = true
            if (savedSettings.sendActivityPushNotification == null) savedSettings.sendActivityPushNotification = true
            if (savedSettings.sendForumPushNotification == null) savedSettings.sendForumPushNotification = true
            if (savedSettings.sendFollowsPushNotification == null) savedSettings.sendFollowsPushNotification = true
            if (savedSettings.sendRelationsPushNotification == null) savedSettings.sendRelationsPushNotification = true
            if (savedSettings.mergePushNotifications == null) savedSettings.mergePushNotifications = false
            if (savedSettings.pushNotificationMinimumHours == null) savedSettings.pushNotificationMinimumHours = 1
            // add more to here when adding new settings
            return savedSettings
        }

    override val userPreferences: UserPreferences
        get() {
            val savedPreferences = localStorage.userPreferences
            if (savedPreferences.seasonalListType == null) savedPreferences.seasonalListType = ListType.LINEAR
            if (savedPreferences.voiceActorLanguage == null) savedPreferences.voiceActorLanguage = StaffLanguage.JAPANESE
            if (savedPreferences.orderCharacterMediaIsDescending == null) savedPreferences.orderCharacterMediaIsDescending = true
            if (savedPreferences.sortStaffAnime == null) savedPreferences.sortStaffAnime = MediaSort.POPULARITY_DESC
            if (savedPreferences.sortStaffManga == null) savedPreferences.sortStaffManga = MediaSort.POPULARITY_DESC
            return savedPreferences
        }

    override fun setAppSettings(value: AppSettings) {
        localStorage.appSettings = value
    }

    override fun setUserPreferences(value: UserPreferences) {
        localStorage.userPreferences = value
    }

    override fun clearStorage() {
        localStorage.clearStorage()
    }
}