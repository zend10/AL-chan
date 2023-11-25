package com.zen.alchan.ui.splash

import com.zen.BuildConfig
import com.zen.alchan.data.repository.InfoRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.Announcement
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.extensions.isSessionExpired
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class SplashViewModel(
    private val userRepository: UserRepository,
    private val infoRepository: InfoRepository
) : BaseViewModel<Unit>() {

    private val _isLoggedIn = PublishSubject.create<Boolean>()
    val isLoggedIn: Observable<Boolean>
        get() = _isLoggedIn

    private val _isSessionExpired = PublishSubject.create<Boolean>()
    val isSessionExpired: Observable<Boolean>
        get() = _isSessionExpired

    private val _updateDialog = PublishSubject.create<Pair<String, Boolean>>()
    val updateDialog: Observable<Pair<String, Boolean>> // String is for message, Boolean is for required update
        get() = _updateDialog

    private val _announcementDialog = PublishSubject.create<String>()
    val announcementDialog: Observable<String> // String is for message
        get() = _announcementDialog

    private var announcement: Announcement? = null

    override fun loadData(param: Unit) {
        loadAnnouncement()
    }

    fun goToNextPage() {
        checkIsLoggedIn()
    }

    fun setNotShowAgain() {
        announcement?.id?.let {
            infoRepository.setLastAnnouncementId(it)
        }
        goToNextPage()
    }

    private fun loadAnnouncement() {
        disposables.add(
            infoRepository.getAnnouncement()
                .zipWith(infoRepository.getLastAnnouncementId()) { announcement, lastAnnouncementId ->
                    announcement to lastAnnouncementId
                }
                .applyScheduler()
                .subscribe(
                    { (announcement, lastAnnouncementId) ->
                        this.announcement = announcement

                        if (announcement.id.isBlank()) {
                            goToNextPage()
                            return@subscribe
                        }

                        if (announcement.appVersion != 0 && announcement.appVersion > BuildConfig.VERSION_CODE) {
                            _updateDialog.onNext(announcement.message to announcement.requiredUpdate)
                            return@subscribe
                        }

                        if (announcement.id == lastAnnouncementId) {
                            goToNextPage()
                            return@subscribe
                        }

                        if (announcement.fromDate.isNotBlank() &&
                            announcement.untilDate.isNotBlank() &&
                            TimeUtil.isBetweenTwoDates(announcement.fromDate, announcement.untilDate)
                        ) {
                            _announcementDialog.onNext(announcement.message)
                            return@subscribe
                        }

                        goToNextPage()
                    },
                    {
                        it.printStackTrace()
                        goToNextPage()
                    }
                )

        )
    }

    private fun checkIsLoggedIn() {
        disposables.add(
            Observable.zip(
                userRepository.getIsAuthenticated(),
                userRepository.getIsLoggedInAsGuest()
            ) { isAuthenticated, isLoggedInAsGuest ->
                isAuthenticated to isLoggedInAsGuest
            }
                .applyScheduler()
                .subscribe { (isAuthenticated, isLoggedInAsGuest) ->
                    if (isAuthenticated) {
                        loadViewerData()
                    } else {
                        _isLoggedIn.onNext(isLoggedInAsGuest)
                    }
                }
        )
    }

    private fun loadViewerData() {
        disposables.add(
            userRepository.getViewer(Source.NETWORK)
                .applyScheduler()
                .subscribe(
                    {
                        _isLoggedIn.onNext(true)
                    },
                    {
                        if (it.isSessionExpired()) {
                            userRepository.logout()
                            _isSessionExpired.onNext(true)
                            _isLoggedIn.onNext(false)
                            return@subscribe
                        }

                        disposables.add(
                            userRepository.getViewer(Source.CACHE)
                                .applyScheduler()
                                .subscribe(
                                    {
                                        _isLoggedIn.onNext(true)
                                    },
                                    {
                                        _isLoggedIn.onNext(false)
                                    }
                                )
                        )
                    }
                )
        )
    }
}