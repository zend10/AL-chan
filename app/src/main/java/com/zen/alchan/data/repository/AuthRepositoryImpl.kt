package com.zen.alchan.data.repository

import com.zen.alchan.R
import com.zen.alchan.data.localstorage.LocalStorage
import com.zen.alchan.helper.enums.AppColorTheme

class AuthRepositoryImpl(private val localStorage: LocalStorage) : AuthRepository {

    override val appColorTheme: Int
        get() = getTheme()


    private fun getTheme(): Int {
        return when (localStorage.appColorTheme) {
            AppColorTheme.YELLOW -> R.style.AppTheme_ThemeYellow
            AppColorTheme.CYAN -> R.style.AppTheme_ThemeCyan
        }
    }
}