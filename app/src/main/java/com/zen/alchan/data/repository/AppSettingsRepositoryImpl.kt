package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.R
import com.zen.alchan.data.localstorage.AppSettingsManager
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.AppSettings
import com.zen.alchan.helper.utils.AndroidUtility
import type.StaffLanguage

class AppSettingsRepositoryImpl(private val appSettingsManager: AppSettingsManager) : AppSettingsRepository {

    override val appColorThemeResource: Int
        get() {
            return when (appSettingsManager.appSettings.appTheme ?: Constant.DEFAULT_THEME) {
                AppColorTheme.YELLOW -> R.style.AppTheme_ThemeYellow
                AppColorTheme.GREEN -> R.style.AppTheme_ThemeGreen
                AppColorTheme.BLUE -> R.style.AppTheme_ThemeBlue
                AppColorTheme.PINK -> R.style.AppTheme_ThemePink
                AppColorTheme.RED -> R.style.AppTheme_ThemeRed
            }
        }

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