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
                AppColorTheme.LIGHT_YELLOW -> R.style.AppTheme_ThemeLightYellow
                AppColorTheme.LIGHT_GREEN -> R.style.AppTheme_ThemeLightGreen
                AppColorTheme.LIGHT_BLUE -> R.style.AppTheme_ThemeLightBlue
                AppColorTheme.LIGHT_PINK -> R.style.AppTheme_ThemeLightPink
                AppColorTheme.LIGHT_RED -> R.style.AppTheme_ThemeLightRed
                AppColorTheme.DARK_YELLOW -> R.style.AppTheme_ThemeDarkYellow
                AppColorTheme.DARK_GREEN -> R.style.AppTheme_ThemeDarkGreen
                AppColorTheme.DARK_BLUE -> R.style.AppTheme_ThemeDarkBlue
                AppColorTheme.DARK_PINK -> R.style.AppTheme_ThemeDarkPink
                AppColorTheme.DARK_RED -> R.style.AppTheme_ThemeDarkRed
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

    override fun setDefaultSetting(isLowOnMemory: Boolean) {
        val newAppSetting = appSettings
        newAppSetting.showSocialTabAutomatically = !isLowOnMemory
        newAppSetting.showBioAutomatically = !isLowOnMemory
        newAppSetting.showStatsAutomatically = !isLowOnMemory
        appSettingsManager.setAppSettings(newAppSetting)
    }

    override fun clearStorage() {
        appSettingsManager.clearStorage()
    }
}