package com.zen.alchan.ui.profile

import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class ProfileViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _currentPage = BehaviorSubject.createDefault(SharedProfileViewModel.Page.BIO)

    val currentPage: Observable<SharedProfileViewModel.Page>
        get() = _currentPage

    override fun loadData() {
        // do nothing
    }

    fun logout() {
        userRepository.logout()
    }

    fun setCurrentPage(page: SharedProfileViewModel.Page) {
        _currentPage.onNext(page)
    }
}