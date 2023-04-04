package com.zen.alchan.ui.medialist

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.Observable.zip
import io.reactivex.subjects.PublishSubject

class BottomSheetMediaListQuickDetailViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel<BottomSheetMediaListQuickDetailParam>() {

    private val _settings = PublishSubject.create<Pair<MediaListOptions, AppSetting>>()
    val settings: Observable<Pair<MediaListOptions, AppSetting>>
        get() = _settings

    override fun loadData(param: BottomSheetMediaListQuickDetailParam) {
        loadOnce {
            val isViewer = param.userId == 0

            disposables.add(
                zip(
                    if (isViewer) userRepository.getViewer(Source.CACHE) else browseRepository.getUser(param.userId),
                    userRepository.getAppSetting()
                ) { user, appSetting ->
                    user.mediaListOptions to appSetting
                }
                    .applyScheduler()
                    .subscribe {
                        _settings.onNext(it)
                    }
            )
        }
    }
}