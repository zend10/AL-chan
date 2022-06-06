package com.zen.alchan.ui.staff.media

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.MediaEdge
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaSort

class StaffMediaListViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel() {

    private val _appSetting = PublishSubject.create<AppSetting>()
    val appSetting: Observable<AppSetting>
        get() = _appSetting

    private val _media = BehaviorSubject.createDefault<List<MediaEdge>>(listOf())
    val media: Observable<List<MediaEdge>>
        get() = _media

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private var staffId = 0
    private var mediaSort = MediaSort.POPULARITY_DESC

    private var hasNextPage = false
    private var currentPage = 0

    override fun loadData() {
        // do nothing
    }

    fun loadData(staffId: Int) {
        loadOnce {
            this.staffId = staffId

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        _appSetting.onNext(it)
                        loadMedia()
                    }
            )
        }
    }

    fun reloadData() {
        loadMedia()
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentMedia = ArrayList(_media.value ?: listOf())
            currentMedia.add(null)
            _media.onNext(currentMedia)

            loadMedia(true)
        }
    }

    private fun loadMedia(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        disposables.add(
            browseRepository.getStaff(staffId, if (isLoadingNextPage) currentPage + 1 else 1, staffMediaSort = listOf(mediaSort))
                .applyScheduler()
                .doFinally {
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                        _emptyLayoutVisibility.onNext(_media.value.isNullOrEmpty())
                    }
                }
                .subscribe(
                    { staff ->
                        hasNextPage = staff.staffMedia.pageInfo.hasNextPage
                        currentPage = staff.staffMedia.pageInfo.currentPage

                        if (isLoadingNextPage) {
                            val currentMedia = ArrayList(_media.value ?: listOf())
                            currentMedia.remove(null)
                            currentMedia.addAll(staff.staffMedia.edges)
                            _media.onNext(currentMedia)
                        } else {
                            _media.onNext(staff.staffMedia.edges)
                        }

                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state  = State.ERROR
                    }
                )
        )
    }
}