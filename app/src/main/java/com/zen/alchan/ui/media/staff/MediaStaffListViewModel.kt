package com.zen.alchan.ui.media.staff

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.data.response.anilist.StaffEdge
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MediaStaffListViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel() {

    private val _appSetting = PublishSubject.create<AppSetting>()
    val appSetting: Observable<AppSetting>
        get() = _appSetting

    private val _staff = BehaviorSubject.createDefault<List<StaffEdge>>(listOf())
    val staff: Observable<List<StaffEdge>>
        get() = _staff

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private var mediaId = 0

    private var hasNextPage = false
    private var currentPage = 0

    override fun loadData() {
        // do nothing
    }

    fun loadData(mediaId: Int) {
        loadOnce {
            this.mediaId = mediaId

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        _appSetting.onNext(it)
                        loadStaffs()
                    }
            )
        }
    }

    fun reloadData() {
        loadStaffs()
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentStaffs = ArrayList(_staff.value ?: listOf())
            currentStaffs.add(null)
            _staff.onNext(currentStaffs)

            loadStaffs(true)
        }
    }

    private fun loadStaffs(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        disposables.add(
            browseRepository.getMediaStaff(mediaId, if (isLoadingNextPage) currentPage + 1 else 1)
                .applyScheduler()
                .doFinally {
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                        _emptyLayoutVisibility.onNext(_staff.value.isNullOrEmpty())
                    }
                }
                .subscribe(
                    { (pageInfo, staffEdges) ->
                        hasNextPage = pageInfo.hasNextPage
                        currentPage = pageInfo.currentPage

                        if (isLoadingNextPage) {
                            val currentStaffs = ArrayList(_staff.value ?: listOf())
                            currentStaffs.remove(null)
                            currentStaffs.addAll(staffEdges)
                            _staff.onNext(currentStaffs)
                        } else {
                            _staff.onNext(staffEdges)
                        }

                        state = State.LOADED
                    },
                    {
                        if (isLoadingNextPage) {
                            val currentStaffs = ArrayList(_staff.value ?: listOf())
                            currentStaffs.remove(null)
                            _staff.onNext(currentStaffs)
                        }

                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }
}