package com.zen.alchan.ui.activity

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.SocialAdapterComponent
import com.zen.alchan.helper.pojo.SocialItem
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

data class ActivityDetailViewModel(
    private val userRepository: UserRepository,
    private val socialRepository: SocialRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel<ActivityDetailParam>() {

    private val _adapterComponent = PublishSubject.create<SocialAdapterComponent>()
    val adapterComponent: Observable<SocialAdapterComponent>
        get() = _adapterComponent

    private val _socialItemList = BehaviorSubject.createDefault(listOf<SocialItem>())
    val socialItemList: Observable<List<SocialItem>>
        get() = _socialItemList

    private var activityId = 0

    override fun loadData(param: ActivityDetailParam) {
        loadOnce {
            activityId = param.activityId

            disposables.add(
                userRepository.getAppSetting()
                    .zipWith(userRepository.getViewer(Source.CACHE).onErrorReturn { null }) { appSetting, viewer ->
                        SocialAdapterComponent(viewer = viewer, appSetting = appSetting)
                    }
                    .applyScheduler()
                    .subscribe {
                        _adapterComponent.onNext(it)
                        loadActivity()
                    }
            )
        }
    }

    fun reloadData() {
        loadActivity()
    }

    private fun loadActivity() {
        _loading.onNext(true)
        state = State.LOADING

        disposables.add(
            socialRepository.getActivityDetail(activityId)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    {
                        val newSocialItems = ArrayList<SocialItem>()
                        newSocialItems.add(SocialItem(activity = it, viewType = SocialItem.VIEW_TYPE_ACTIVITY))
                        newSocialItems.addAll(
                            it.replies.map {
                                SocialItem(activityReply = it, viewType = SocialItem.VIEW_TYPE_ACTIVITY_REPLY)
                            }
                        )
                        _socialItemList.onNext(newSocialItems)
                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }
}