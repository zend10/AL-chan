package com.zen.alchan.ui.landing

import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.NavigationManager

class LandingViewModel : BaseViewModel() {

    fun pressGetStarted() {
        navigationSubject.onNext(NavigationManager.Page.LOGIN to listOf())
    }
}