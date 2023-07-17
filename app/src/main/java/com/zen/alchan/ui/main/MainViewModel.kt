package com.zen.alchan.ui.main

import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.service.pushnotification.PushNotificationService
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class MainViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository,
    private val pushNotificationService: PushNotificationService
) : BaseViewModel<Unit>() {

    val isViewerAuthenticated: Boolean
        get() = userRepository.getIsAuthenticated().blockingFirst()

    private val _unreadNotificationCount = BehaviorSubject.createDefault(0)
    val unreadNotificationCount: Observable<Int>
        get() = _unreadNotificationCount

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                contentRepository.getGenres().subscribe({}, {})
            )

            disposables.add(
                contentRepository.getTags().subscribe({}, {})
            )

            disposables.add(
                userRepository.unreadNotificationCount
                    .applyScheduler()
                    .subscribe {
                        _unreadNotificationCount.onNext(it)
                    }
            )

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        pushNotificationService.startPushNotification()
                    }
            )
        }
    }

    fun clearUnreadNotificationCountBadge() {
        userRepository.clearUnreadNotificationCount()
    }
}