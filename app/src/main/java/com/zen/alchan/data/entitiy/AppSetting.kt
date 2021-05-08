package com.zen.alchan.data.entitiy

import com.zen.alchan.helper.enums.AppTheme

data class AppSetting(
    var appTheme: AppTheme = AppTheme.DEFAULT_THEME_YELLOW
) {
    companion object {
        val EMPTY_APP_SETTING = AppSetting()
    }
}