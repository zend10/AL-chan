package com.zen.alchan.ui.landing

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.NavigationManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class LandingViewModel(private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

}