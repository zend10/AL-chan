package com.zen.alchan.ui.follow

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class FollowViewModel(
    private val userRepository: UserRepository
) : BaseViewModel<FollowParam>() {

    private val _followAdapterComponent = PublishSubject.create<AppSetting>()
    val followAdapterComponent: Observable<AppSetting>
        get() = _followAdapterComponent

    private val _users = BehaviorSubject.createDefault<List<User?>>(listOf())
    val users: Observable<List<User?>>
        get() = _users

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private var isViewer = false
    private var viewerId = 0
    private var userId = 0
    private var isFollowingScreen = false

    private var hasNextPage = false
    private var currentPage = 0

    override fun loadData(param: FollowParam) {
        loadOnce {
            userId = param.userId
            isFollowingScreen = param.isFollowingScreen

            disposables.add(
                userRepository.getAppSetting()
                    .zipWith(userRepository.getViewer(Source.CACHE)) { appSetting, user ->
                        return@zipWith appSetting to user
                    }
                    .applyScheduler()
                    .subscribe { (appSetting, user) ->
                        _followAdapterComponent.onNext(appSetting)
                        viewerId = user.id
                        isViewer = userId == 0 || userId == viewerId

                        if (userId == 0)
                            this.userId = viewerId

                        loadFollowList()
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

    fun toggleFollow(user: User) {
        if (user.id == viewerId) {
            _error.onNext(R.string.did_you_just_try_to_follow_yourself)
            return
        }

        _loading.onNext(true)

        disposables.add(
            userRepository.toggleFollow(user.id)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { isFollowing ->
                        val currentUsers = ArrayList(_users.value ?: listOf())
                        val userIndex = currentUsers.indexOfFirst { currentUser -> currentUser?.id == user.id }
                        if (userIndex == -1) return@subscribe

                        // is isViewer and isFollowingScreen and isFollowing false
                        if (isViewer && isFollowingScreen && !isFollowing) {
                            currentUsers.removeAt(userIndex)
                        } else {
                            currentUsers[userIndex]?.isFollowing = isFollowing
                        }

                        _users.onNext(currentUsers)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    private fun loadFollowList(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
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
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                        _emptyLayoutVisibility.onNext(_users.value.isNullOrEmpty())
                    }
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