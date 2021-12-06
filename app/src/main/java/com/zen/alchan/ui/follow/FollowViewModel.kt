package com.zen.alchan.ui.follow

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class FollowViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _followAdapterComponent = PublishSubject.create<AppSetting>()
    val followAdapterComponent: Observable<AppSetting>
        get() = _followAdapterComponent

    private val _users = BehaviorSubject.createDefault<List<User?>>(listOf())
    val users: Observable<List<User?>>
        get() = _users

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private var userId = 0
    private var isFollowingScreen = false

    private var hasNextPage = false
    private var currentPage = 0

    override fun loadData() {
        // do nothing
    }

    fun loadData(userId: Int, isFollowingScreen: Boolean) {
        loadOnce {
            this.userId = userId
            this.isFollowingScreen = isFollowingScreen

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe { appSetting ->
                        _followAdapterComponent.onNext(appSetting)

                        if (userId == 0) {
                            disposables.add(
                                userRepository.getViewer(Source.CACHE)
                                    .applyScheduler()
                                    .subscribe { user ->
                                        this.userId = user.id
                                        loadFollowList()
                                    }
                            )
                        } else {
                            loadFollowList()
                        }
                    }
            )
        }
    }

    fun reloadData() {
        loadFollowList()
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentUsers = ArrayList(_users.value ?: listOf())
            currentUsers.add(null)
            _users.onNext(currentUsers)

            loadFollowList(true)
        }
    }

    private fun loadFollowList(isLoadingNextPage: Boolean = false) {
        _loading.onNext(true)
        state = State.LOADING

        val followObservable = if (isFollowingScreen)
            userRepository.getFollowing(userId, if (isLoadingNextPage) currentPage + 1 else 1)
        else
            userRepository.getFollowers(userId, if (isLoadingNextPage) currentPage + 1 else 1)

        disposables.add(
            followObservable
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                    _emptyLayoutVisibility.onNext(_users.value.isNullOrEmpty())
                }
                .subscribe(
                    { (pageInfo, users) ->
                        hasNextPage = pageInfo.hasNextPage
                        currentPage = pageInfo.currentPage

                        if (isLoadingNextPage) {
                            val currentUsers = ArrayList(_users.value ?: listOf())
                            currentUsers.remove(null)
                            currentUsers.addAll(users)
                            _users.onNext(currentUsers)
                        } else {
                            _users.onNext(users)
                        }

                        state = State.LOADED
                    },
                    {
                        if (isLoadingNextPage) {
                            val currentUsers = ArrayList(_users.value ?: listOf())
                            currentUsers.remove(null)
                            _users.onNext(currentUsers)
                        }

                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }
}