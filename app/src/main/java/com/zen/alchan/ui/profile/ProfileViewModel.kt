package com.zen.alchan.ui.profile

import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ProfileViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _currentPage = BehaviorSubject.createDefault(Page.BIO)
    val currentPage: Observable<Page>
        get() = _currentPage

    private val _isViewerProfile = BehaviorSubject.createDefault(false)
    val isViewerProfile: Observable<Boolean>
        get() = _isViewerProfile

    var userId = 0

    override fun loadData() {
        load {
            checkIsAuthenticated()
            checkIsViewerProfile()
        }
    }

    fun logout() {
        userRepository.logoutAsGuest()
    }

    fun setCurrentPage(page: Page) {
        _currentPage.onNext(page)
    }

    private fun checkIsAuthenticated() {
        disposables.add(
            userRepository.getIsAuthenticated()
                .applyScheduler()
                .subscribe {
                    _isAuthenticated.onNext(it)
                }
        )
    }

    private fun checkIsViewerProfile() {
        _isViewerProfile.onNext(userId == 0)
    }

    enum class Page {
        BIO,
        FAVORITE,
        STATS,
        REVIEW
    }
}