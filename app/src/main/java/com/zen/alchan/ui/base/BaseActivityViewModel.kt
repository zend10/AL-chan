package com.zen.alchan.ui.base

import com.zen.alchan.R
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.extensions.applyScheduler
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class BaseActivityViewModel(private val userRepository: UserRepository) : BaseViewModel<Unit>() {

    override fun loadData(param: Unit) = Unit

    fun isLightMode(): Boolean {
        return userRepository.getAppTheme().name.contains("LIGHT")
    }

    // TODO: Readd back for dev
    fun getAppThemeResource(): Int {
        return when (userRepository.getAppTheme()) {
            AppTheme.DEFAULT_THEME_YELLOW -> R.style.AppTheme_ThemeDefaultYellow
            AppTheme.DEFAULT_THEME_BLUE -> R.style.AppTheme_ThemeDefaultBlue
//            AppTheme.DEFAULT_THEME_PURPLE -> 0
            AppTheme.DEFAULT_THEME_GREEN -> R.style.AppTheme_ThemeDefaultGreen
//            AppTheme.DEFAULT_THEME_ORANGE -> 0
            AppTheme.DEFAULT_THEME_RED -> R.style.AppTheme_ThemeDefaultRed
            AppTheme.DEFAULT_THEME_PINK -> R.style.AppTheme_ThemeDefaultPink
            AppTheme.LIGHT_THEME_BLUE -> R.style.AppTheme_ThemeLightBlue
//            AppTheme.LIGHT_THEME_PURPLE -> 0
            AppTheme.LIGHT_THEME_GREEN -> R.style.AppTheme_ThemeLightGreen
            AppTheme.LIGHT_THEME_ORANGE -> R.style.AppTheme_ThemeLightOrange
            AppTheme.LIGHT_THEME_RED -> R.style.AppTheme_ThemeLightRed
            AppTheme.LIGHT_THEME_PINK -> R.style.AppTheme_ThemeLightPink
            AppTheme.DARK_THEME_YELLOW -> R.style.AppTheme_ThemeDarkYellow
            AppTheme.DARK_THEME_BLUE -> R.style.AppTheme_ThemeDarkBlue
//            AppTheme.DARK_THEME_PURPLE -> 0
            AppTheme.DARK_THEME_GREEN -> R.style.AppTheme_ThemeDarkGreen
//            AppTheme.DARK_THEME_ORANGE -> 0
            AppTheme.DARK_THEME_RED -> R.style.AppTheme_ThemeDarkRed
            AppTheme.DARK_THEME_PINK -> R.style.AppTheme_ThemeDarkPink
            AppTheme.ANILIST_LIGHT_BLUE -> R.style.AppTheme_ThemeAniListLightBlue
            AppTheme.ANILIST_LIGHT_PURPLE -> R.style.AppTheme_ThemeAniListLightPurple
            AppTheme.ANILIST_LIGHT_GREEN -> R.style.AppTheme_ThemeAniListLightGreen
            AppTheme.ANILIST_LIGHT_ORANGE -> R.style.AppTheme_ThemeAniListLightOrange
            AppTheme.ANILIST_LIGHT_RED -> R.style.AppTheme_ThemeAniListLightRed
            AppTheme.ANILIST_LIGHT_PINK -> R.style.AppTheme_ThemeAniListLightPink
            AppTheme.ANILIST_LIGHT_GREY -> R.style.AppTheme_ThemeAniListLightGrey
            AppTheme.ANILIST_DARK_BLUE -> R.style.AppTheme_ThemeAniListDarkBlue
            AppTheme.ANILIST_DARK_PURPLE -> R.style.AppTheme_ThemeAniListDarkPurple
            AppTheme.ANILIST_DARK_GREEN -> R.style.AppTheme_ThemeAniListDarkGreen
            AppTheme.ANILIST_DARK_ORANGE -> R.style.AppTheme_ThemeAniListDarkOrange
            AppTheme.ANILIST_DARK_RED -> R.style.AppTheme_ThemeAniListDarkRed
            AppTheme.ANILIST_DARK_PINK -> R.style.AppTheme_ThemeAniListDarkPink
            AppTheme.ANILIST_DARK_GREY -> R.style.AppTheme_ThemeAniListDarkGrey
        }
    }
}