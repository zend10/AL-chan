package com.zen.alchan.ui.explore

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ExploreViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : BaseViewModel<SearchCategory>() {

    private val _appSetting = PublishSubject.create<AppSetting>()
    val appSetting: Observable<AppSetting>
        get() = _appSetting

    private var currentSearchCategory = SearchCategory.ANIME

    override fun loadData(param: SearchCategory) {
        this.currentSearchCategory = param

        loadOnce {
            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        _appSetting.onNext(it)
                    }
            )
        }
    }

    fun doSearch(searchQuery: String, isLoadingNextPage: Boolean = false) {

    }
}