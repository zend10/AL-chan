package com.zen.alchan.ui.profile

import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.helper.pojo.BioItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import type.MediaListStatus

class SharedProfileViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val userDataSubject = BehaviorSubject.createDefault(User.EMPTY_USER)
    private val bioItemsSubject = BehaviorSubject.createDefault(listOf<BioItem>())
    private val animeCountSubject = BehaviorSubject.createDefault(0)
    private val mangaCountSubject = BehaviorSubject.createDefault(0)
    private val followingCountSubject = BehaviorSubject.createDefault(0)
    private val followersCountSubject = BehaviorSubject.createDefault(0)

    val userData: Observable<User>
        get() = userDataSubject

    val bioItems: Observable<List<BioItem>>
        get() = bioItemsSubject

    val animeCount: Observable<Int>
        get() = animeCountSubject

    val mangaCount: Observable<Int>
        get() = mangaCountSubject

    val followingCount: Observable<Int>
        get() = followingCountSubject

    val followersCount: Observable<Int>
        get() = followersCountSubject

    var userId = 0

    fun checkIsAuthenticated() {
        disposables.add(
            authenticationRepository.getIsAuthenticated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    loadUserData()
                    isAuthenticatedSubject.onNext(it)
                }
        )
    }

    private fun loadUserData(isReloading: Boolean = false) {
        if (!isReloading && state == State.LOADED) return

        disposables.add(
            authenticationRepository.viewer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    userId = it.id
                    loadProfileData(if (isReloading) Source.NETWORK else null)

                    userDataSubject.onNext(it)

                    state = State.LOADED
                }
        )

        if (userId == 0)
            authenticationRepository.getViewerData()
    }

    private fun loadProfileData(source: Source?) {
        disposables.add(
            userRepository.getProfileData(userId, source = source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        bioItemsSubject.onNext(
                            listOf(
                                BioItem(viewType = BioItem.VIEW_TYPE_AFFINITY),
                                BioItem(bioText = it.user.about, viewType = BioItem.VIEW_TYPE_ABOUT)
                            )
                        )

                        animeCountSubject.onNext(
                            it.user.statistics.anime.statuses.filter { anime -> anime.status == MediaListStatus.COMPLETED }.size
                        )
                        mangaCountSubject.onNext(
                            it.user.statistics.manga.statuses.filter { manga -> manga.status == MediaListStatus.COMPLETED }.size
                        )
                        followingCountSubject.onNext(
                            it.following.pageInfo.total
                        )
                        followersCountSubject.onNext(
                            it.followers.pageInfo.total
                        )
                    },
                    {
                        errorSubject.onNext(it.sendMessage())
                    }
                )
        )
    }

    enum class Page {
        BIO,
        FAVORITE,
        STATS,
        ACTIVITY
    }
}