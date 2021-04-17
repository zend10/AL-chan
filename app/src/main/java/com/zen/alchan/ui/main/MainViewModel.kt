package com.zen.alchan.ui.main

import com.apollographql.apollo.exception.ApolloHttpException
import com.zen.alchan.data.model.Media
import com.zen.alchan.data.model.Review
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MainViewModel(private val contentRepository: ContentRepository) : BaseViewModel() {


}