package com.zen.alchan.ui.settings.app

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class AppSettingsViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val appSettingSubject = BehaviorSubject.createDefault(AppSetting.EMPTY_APP_SETTING)

    val appSetting: Observable<AppSetting>
        get() = appSettingSubject

    override fun loadData() {
        getAppSetting()
    }

    private fun getAppSetting() {
        disposables.add(
            userRepository.getAppSetting()
                .applyScheduler()
                .subscribe {
                    appSettingSubject.onNext(it)
                }
        )
    }
}