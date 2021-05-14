package com.zen.alchan.ui.settings.app

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.selects.select

class AppSettingsViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val appThemeSubject = BehaviorSubject.createDefault(AppTheme.DEFAULT_THEME_YELLOW)

    val appTheme: Observable<AppTheme>
        get() = appThemeSubject

    private var currentAppSetting: AppSetting? = null

    private var selectedAppTheme: AppTheme? = null

    override fun loadData() {
        getAppSetting()
    }

    fun updateAppTheme(newAppTheme: AppTheme) {
        selectedAppTheme = newAppTheme
        appThemeSubject.onNext(newAppTheme)
    }

    private fun getAppSetting() {
        disposables.add(
            userRepository.getAppSetting()
                .applyScheduler()
                .subscribe {
                    currentAppSetting = it
                    updateAppTheme(it.appTheme)
                }
        )
    }
}