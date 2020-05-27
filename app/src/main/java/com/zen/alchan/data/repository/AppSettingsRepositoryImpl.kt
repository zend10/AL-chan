package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.localstorage.AppSettingsManager
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.AppSettings
import com.zen.alchan.helper.utils.AndroidUtility
import type.StaffLanguage

class AppSettingsRepositoryImpl(private val appSettingsManager: AppSettingsManager) : AppSettingsRepository {

    override val appColorThemeResource: Int
        get() = AndroidUtility.getAppColorThemeRes(appSettingsManager.appSettings.appTheme)

    private val _appColorThemeLiveData = SingleLiveEvent<Int>()
    override val appColorThemeLiveData: LiveData<Int>
        get() = _appColorThemeLiveData

    override val appSettings: AppSettings
        get() = appSettingsManager.appSettings

    override fun setAppSettings(appSettings: AppSettings) {
        appSettingsManager.setAppSettings(appSettings)
        _appColorThemeLiveData.postValue(appColorThemeResource)
    }

    override fun clearStorage() {
        appSettingsManager.clearStorage()
    }
}