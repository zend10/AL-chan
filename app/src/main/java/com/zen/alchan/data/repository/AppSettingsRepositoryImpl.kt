package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.localstorage.AppSettingsManager
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.PushNotificationsSettings
import com.zen.alchan.helper.utils.AndroidUtility
import type.StaffLanguage

class AppSettingsRepositoryImpl(private val appSettingsManager: AppSettingsManager) : AppSettingsRepository {

    override val appColorThemeResource: Int
        get() = AndroidUtility.getAppColorThemeRes(appSettingsManager.appColorTheme)

    private val _appColorThemeLiveData = SingleLiveEvent<Int>()
    override val appColorThemeLiveData: LiveData<Int>
        get() = _appColorThemeLiveData

    override val appColorTheme: AppColorTheme
        get() = appSettingsManager.appColorTheme

    override val voiceActorLanguage: StaffLanguage
        get() = appSettingsManager.voiceActorLanguage

    override val pushNotificationsSettings: PushNotificationsSettings
        get() = appSettingsManager.pushNotificationsSettings

    override fun setAppSettings(
        appColorTheme: AppColorTheme,
        voiceActorLanguage: StaffLanguage
    ) {
        appSettingsManager.apply {
            setAppColorTheme(appColorTheme)
            setVoiceActorLanguage(voiceActorLanguage)
        }
        _appColorThemeLiveData.postValue(appColorThemeResource)
    }

    override fun setPushNotificationsSettings(
        pushNotifAiring: Boolean,
        pushNotifActivity: Boolean,
        pushNotifForum: Boolean,
        pushNotifFollows: Boolean
    ) {
        appSettingsManager.setPushNotificationsSettings(PushNotificationsSettings(pushNotifAiring, pushNotifActivity, pushNotifForum, pushNotifFollows))
    }
}