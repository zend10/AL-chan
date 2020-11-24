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
                AppColorTheme.DEFAULT_THEME_YELLOW -> R.style.AppTheme_ThemeYellow
                AppColorTheme.DEFAULT_THEME_GREEN -> R.style.AppTheme_ThemeGreen
                AppColorTheme.DEFAULT_THEME_BLUE -> R.style.AppTheme_ThemeBlue
                AppColorTheme.DEFAULT_THEME_PINK -> R.style.AppTheme_ThemePink
                AppColorTheme.DEFAULT_THEME_RED -> R.style.AppTheme_ThemeRed
                AppColorTheme.LIGHT_THEME_YELLOW -> R.style.AppTheme_ThemeLightYellow
                AppColorTheme.LIGHT_THEME_GREEN -> R.style.AppTheme_ThemeLightGreen
                AppColorTheme.LIGHT_THEME_BLUE -> R.style.AppTheme_ThemeLightBlue
                AppColorTheme.LIGHT_THEME_PINK -> R.style.AppTheme_ThemeLightPink
                AppColorTheme.LIGHT_THEME_RED -> R.style.AppTheme_ThemeLightRed
                AppColorTheme.DARK_THEME_YELLOW -> R.style.AppTheme_ThemeDarkYellow
                AppColorTheme.DARK_THEME_GREEN -> R.style.AppTheme_ThemeDarkGreen
                AppColorTheme.DARK_THEME_BLUE -> R.style.AppTheme_ThemeDarkBlue
                AppColorTheme.DARK_THEME_PINK -> R.style.AppTheme_ThemeDarkPink
                AppColorTheme.DARK_THEME_RED -> R.style.AppTheme_ThemeDarkRed
                AppColorTheme.ANILIST_LIGHT_BLUE -> R.style.AppTheme_ThemeAniListLightBlue
                AppColorTheme.ANILIST_LIGHT_PURPLE -> R.style.AppTheme_ThemeAniListLightPurple
                AppColorTheme.ANILIST_LIGHT_GREEN -> R.style.AppTheme_ThemeAniListLightGreen
                AppColorTheme.ANILIST_LIGHT_ORANGE -> R.style.AppTheme_ThemeAniListLightOrange
                AppColorTheme.ANILIST_LIGHT_RED -> R.style.AppTheme_ThemeAniListLightRed
                AppColorTheme.ANILIST_LIGHT_PINK -> R.style.AppTheme_ThemeAniListLightPink
                AppColorTheme.ANILIST_LIGHT_GREY -> R.style.AppTheme_ThemeAniListLightGrey
                AppColorTheme.ANILIST_DARK_BLUE -> R.style.AppTheme_ThemeAniListDarkBlue
                AppColorTheme.ANILIST_DARK_PURPLE -> R.style.AppTheme_ThemeAniListDarkPurple
                AppColorTheme.ANILIST_DARK_GREEN -> R.style.AppTheme_ThemeAniListDarkGreen
                AppColorTheme.ANILIST_DARK_ORANGE -> R.style.AppTheme_ThemeAniListDarkOrange
                AppColorTheme.ANILIST_DARK_RED -> R.style.AppTheme_ThemeAniListDarkRed
                AppColorTheme.ANILIST_DARK_PINK -> R.style.AppTheme_ThemeAniListDarkPink
                AppColorTheme.ANILIST_DARK_GREY -> R.style.AppTheme_ThemeAniListDarkGrey
                AppColorTheme.COMMUNITY_AXIEL_BLUE -> R.style.AppTheme_ThemeAxielBlue
                AppColorTheme.COMMUNITY_SAM_ORANGE -> R.style.AppTheme_ThemeSamOrange
                AppColorTheme.COMMUNITY_DARK_AXIEL_BLUE -> R.style.AppTheme_ThemeDarkAxielBlue
                AppColorTheme.COMMUNITY_DARK_SAM_ORANGE -> R.style.AppTheme_ThemeDarkSamOrange
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