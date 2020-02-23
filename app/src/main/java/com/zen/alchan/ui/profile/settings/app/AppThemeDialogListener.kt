package com.zen.alchan.ui.profile.settings.app

import com.zen.alchan.helper.enums.AppColorTheme

interface AppThemeDialogListener {
    fun passSelectedTheme(theme: AppColorTheme)
}