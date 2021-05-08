package com.zen.alchan.ui.profile

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class ProfileViewModel(private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

    private val currentPageSubject = BehaviorSubject.createDefault(SharedProfileViewModel.Page.BIO)

    val currentPage: Observable<SharedProfileViewModel.Page>
        get() = currentPageSubject

    override fun loadData() {
        // do nothing
    }

    fun logoutAsGuest() {
        authenticationRepository.loginAsGuest(false)
    }

    fun setCurrentPage(page: SharedProfileViewModel.Page) {
        currentPageSubject.onNext(page)
    }
}